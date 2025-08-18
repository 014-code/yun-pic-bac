package com.mashang.yunbac.web.aop;

import com.mashang.yunbac.web.annotation.AuthCheck;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.enums.UserRoleEnum;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.service.YunUserService;
import com.mashang.yunbac.web.utils.JWTUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component("authAspect")
public class AuthAspect {

    @Resource
    private YunUserService yunUserService;

    /**
     * 拦截带有自定义权限校验注解的方法，使用环绕通知，的拦截具体方法
     *
     * @param pj
     * @param authCheck
     * @return
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint pj, AuthCheck authCheck) throws Throwable {
        //拿到注解需要的的用户角色(默认不在注解传入为用户角色)
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        //拿到当前登录用户
        String token = (String) request.getAttribute("Authorization");
        Long userId = JWTUtil.getUserId(token);
        YunUser yunUser = yunUserService.getById(userId);
        //获得枚举角色
        UserRoleEnum enumByValue = UserRoleEnum.getEnumByValue(mustRole);
        //不需要权限则直接放行
        if (enumByValue == null) {
            return pj.proceed();
        }
        //当前登录用户权限(角色)-枚举
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(yunUser.getRole());
        //用户没角色则拒接
        if (userRoleEnum == null) {
            //抛异常
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //若用户当前角色与注解要求角色不同则拒绝
        if (!userRoleEnum.getValue().equals(enumByValue.getValue())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //最终全部通过则放行
        return pj.proceed();
    }

}
