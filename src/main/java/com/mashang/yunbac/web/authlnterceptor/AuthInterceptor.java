package com.mashang.yunbac.web.authlnterceptor;

import com.mashang.yunbac.web.manger.RedisManger;
import com.mashang.yunbac.web.utils.JWTUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.annotation.Resource;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private RedisManger redisManger;

    /**
     * 要验证用户是否有权限，那么就要验证token
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestUri = request.getRequestURI();
        // 白名单路径（无需登录）
        if (requestUri.startsWith("/api/user/login")
                || requestUri.startsWith("/api/user/register")
                || requestUri.startsWith("/api/doc.html")
                || requestUri.startsWith("/api/swagger-ui.html")
                || requestUri.startsWith("/api/swagger-resources")
                || requestUri.startsWith("/api/webjars/")
                || requestUri.matches(".*/api-docs.*")
                || requestUri.startsWith("/api/error")) {
            return true;
        }

        // 从头部获取我们的token
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            // 尝试从 Cookie 中读取 Authorization
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("Authorization".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");

        if (token == null || token.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或缺少token\"}");
            return false;
        }

        boolean valid = JWTUtil.verifyToken(token);
        if (!valid) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"用户认证失败\"}");
            return false;
        }

        Long userId = JWTUtil.getUserId(token);
        if (userId == null || userId == 0L) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"用户不存在或token过期\"}");
            return false;
        }

        // 校验 Redis 中的会话是否存在（支持服务端踢人/过期）
        String redisKey = "login:token:" + userId;
        String serverToken = redisManger.get(redisKey);
        if (serverToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"登录态已过期，请重新登录\"}");
            return false;
        }
        // 可选：比对header的token与服务端保存是否一致（单端登录）
        if (!serverToken.equals(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"账号在其他地方登录\"}");
            return false;
        }

        request.setAttribute("Authorization", token);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

}
