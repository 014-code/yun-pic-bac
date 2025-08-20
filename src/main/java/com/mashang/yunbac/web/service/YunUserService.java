package com.mashang.yunbac.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.params.user.LoginParam;
import com.mashang.yunbac.web.entity.params.user.RegisterParam;
import com.mashang.yunbac.web.utils.ResultTUtil;

/**
 * (YunUser)表服务接口
 *
 * @author makejava
 * @since 2025-08-18 11:03:26
 */
public interface YunUserService extends IService<YunUser> {

    /**
     * 用户注册
     *
     * @param registerParam
     * @return
     */
    ResultTUtil<String> register(RegisterParam registerParam);

    /**
     * 用户登录
     *
     * @param loginParam
     * @return
     */
    ResultTUtil<String> login(LoginParam loginParam);

}
