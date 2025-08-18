package com.mashang.yunbac.web.authlnterceptor;

import com.mashang.yunbac.web.utils.JWTUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

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
        // 从头部获取我们的token
        String token = request.getHeader("Authorization");
        // 设置返回值类型
        response.setContentType("text/plain;charset=utf-8");
        // 如果token是空的就说明没有token，没有权限
        if (token == null || "".equals(token)) {
            response.getWriter().write("没有token");
            return false;
        }
        boolean b = JWTUtil.verifyToken(token);
        if (!b) {
            response.getWriter().write("用户认证失败");
            return false;
        }
        // 统一处理用户id不存在的情况
        Long userId = JWTUtil.getUserId(token);
        if (userId == null) {
            response.getWriter().write("用户不存在或者过期");
            return false;
        }
        request.setAttribute("Authorization", token);
        return b;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

}
