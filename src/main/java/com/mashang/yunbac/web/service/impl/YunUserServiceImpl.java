package com.mashang.yunbac.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.enums.UserRoleEnum;
import com.mashang.yunbac.web.entity.params.user.LoginParam;
import com.mashang.yunbac.web.entity.params.user.RegisterParam;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.mapper.YunUserMapper;
import com.mashang.yunbac.web.service.YunUserService;
import com.mashang.yunbac.web.utils.JWTUtil;
import com.mashang.yunbac.web.utils.ResultTUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * (YunUser)表服务实现类
 *
 * @author makejava
 * @since 2025-08-18 11:03:26
 */
@Service("yunUserService")
public class YunUserServiceImpl extends ServiceImpl<YunUserMapper, YunUser> implements YunUserService {
    @Resource
    private YunUserMapper yunUserMapper;

    // 加密前缀
    private static final String SALT = "user";
    // 用户账号的正则 - 匹配特殊字符
    private static final String ACCOUNT_REGEX = ".*[`~!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";

    /**
     * 用户注册
     *
     * @return
     */
    @Override
    public ResultTUtil<String> register(RegisterParam registerParam) {
        //账号正则
        if (Pattern.matches(ACCOUNT_REGEX, registerParam.getAccount()))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符!");
        //判断账号是否重复
        QueryWrapper<YunUser> yunUserQueryWrapper = new QueryWrapper<>();
        yunUserQueryWrapper.eq("account", registerParam.getAccount());
        //这里使用this指向和使用注入mapper效果相同
        Long count = yunUserMapper.selectCount(yunUserQueryWrapper);
        if (count > 0) throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已存在!");
        //加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + registerParam.getPassword()).getBytes());
        YunUser msUser = new YunUser();
        msUser.setUserName(registerParam.getUserName());
        msUser.setAccount(registerParam.getAccount());
        msUser.setPassword(newPassword);
        msUser.setUserName("默认名称");
        msUser.setRole(UserRoleEnum.USER.getValue());
        msUser.setProfile("这个用户很懒，没写简介");
        msUser.setCreateTime(new Date());
        msUser.setUpdateTime(new Date());
        msUser.setCreateBy(registerParam.getAccount());
        msUser.setUpdateBy(registerParam.getAccount());
        //插入
        int insert = yunUserMapper.insert(msUser);
        if (insert <= 0) throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        return new ResultTUtil<String>().success("注册成功");
    }

    @Override
    public ResultTUtil<String> login(LoginParam loginParam) {
        //加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + loginParam.getPassword()).getBytes());
        //查询一致的结果
        QueryWrapper<YunUser> qw = new QueryWrapper<>();
        qw.eq("account", loginParam.getAccount());
        qw.eq("password", newPassword);
        YunUser user = yunUserMapper.selectOne(qw);
        if (user == null) throw new BusinessException(ErrorCode.SYSTEM_ERROR, "账号或密码错误!");
        //创建token
        String token = JWTUtil.createToken(user);
        //登录成功
        return new ResultTUtil<String>().success("登录成功", token);
    }

}
