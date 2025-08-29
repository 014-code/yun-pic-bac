package com.mashang.yunbac.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mashang.yunbac.web.entity.domian.YunSpace;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.enums.SpaceEnum;
import com.mashang.yunbac.web.entity.params.space.AddSpaceParam;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.exception.ThrowUtils;
import com.mashang.yunbac.web.mapper.YunSpaceMapper;
import com.mashang.yunbac.web.mapper.YunUserMapper;
import com.mashang.yunbac.web.service.YunSpaceService;
import com.mashang.yunbac.web.utils.JWTUtil;
import com.mashang.yunbac.web.utils.ResultTUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * (YunSpace)表服务实现类
 *
 * @author makejava
 * @since 2025-08-29 12:27:49
 */
@Service("yunSpaceService")
public class YunSpaceServiceImpl extends ServiceImpl<YunSpaceMapper, YunSpace> implements YunSpaceService {

    @Resource
    private YunUserMapper yunUserMapper;
    @Resource
    private TransactionTemplate transactionTemplate;


    @Override
    public ResultTUtil addSpace(AddSpaceParam param, HttpServletRequest request) {
        //校验参数
        ThrowUtils.throwIf(param.getSpaceLevel() == null, ErrorCode.PARAMS_ERROR, "空间类型不能为空");
        //判断用户创建的空间类别是哪种-用户只能普通
        //拿到当前登录用户
        String token = request.getHeader("Authorization");
        Long userId = JWTUtil.getUserId(token);
        YunUser yunUser = yunUserMapper.selectById(userId);
        //查看要创建的空间id是否存在
        if (!SpaceEnum.getValues().contains(param.getSpaceLevel())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无此空间");
        }
        //如果不是普通空间
        if (!param.getSpaceLevel().equals(1) && !yunUser.getRole().equals("admin")) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建该空间");
        }
        // 针对用户加锁
        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            //事务编程
            Long execute = transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery().eq(YunSpace::getUserId, userId).exists();
                //用户是管理员则跳过
                if (!yunUser.getRole().equals("admin")) {
                    ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有空间");
                }
                param.setUserId(userId);
                //设置对应类型空间大小
                param.setMaxSize(SpaceEnum.getEnumByValue(param.getSpaceLevel()).getMaxSize());
                param.setMaxCount(SpaceEnum.getEnumByValue(param.getSpaceLevel()).getMaxCount());
                YunSpace yunSpace = new YunSpace();
                BeanUtils.copyProperties(param, yunSpace);
                yunSpace.setSpaceLevel(String.valueOf(param.getSpaceLevel()));
                //插值
                boolean save = this.save(yunSpace);
                ThrowUtils.throwIf(save, ErrorCode.OPERATION_ERROR);
                return yunSpace.getSpaceId();
            });
            return new ResultTUtil().success("创建成功");
        }
    }
}

