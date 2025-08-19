package com.mashang.yunbac.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mashang.yunbac.web.annotation.AuthCheck;
import com.mashang.yunbac.web.constant.UserConstant;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.params.*;
import com.mashang.yunbac.web.entity.vo.YunUserVo;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.service.YunUserService;
import com.mashang.yunbac.web.utils.JWTUtil;
import com.mashang.yunbac.web.utils.ResultTUtil;
import com.mashang.yunbac.web.utils.RowsTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * (YunUser)表控制层
 *
 * @author makejava
 * @since 2025-08-18 11:03:24
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "用户模块")
public class YunUserController {
    // 加密前缀
    private static final String SALT = "user";
    // 用户账号的正则 - 匹配特殊字符
    private static final String ACCOUNT_REGEX = ".*[`~!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";
    /**
     * 服务对象
     */
    @Resource
    private YunUserService yunUserService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public ResultTUtil<String> register(@RequestBody RegisterParam registerParam) {
        if (StringUtils.isAllBlank(registerParam.getAccount(), registerParam.getPassword(), registerParam.getRePassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入参数有误");
        }
        return yunUserService.register(registerParam);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ResultTUtil<String> login(@RequestBody LoginParam loginParam) {
        //登录的参数是否有空的
        if (StringUtils.isAllBlank(loginParam.getAccount(), loginParam.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入的参数有空!");
        }
        return yunUserService.login(loginParam);
    }

    @ApiOperation(value = "获取当前用户信息")
    @GetMapping("/info")
    public ResultTUtil<YunUserVo> info(HttpServletRequest request) {
        YunUserVo yunUserVo = new YunUserVo();
        String token = (String) request.getAttribute("Authorization");
        Long userId = JWTUtil.getUserId(token);
        YunUser yunUser = yunUserService.getById(userId);
        BeanUtils.copyProperties(yunUser, yunUserVo);
        return new ResultTUtil<YunUserVo>().success("查询", yunUserVo);
    }

    @ApiOperation("注销用户")
    @PostMapping("/cancellation")
    public ResultTUtil cancellation(HttpServletRequest request) {
        String token = (String) request.getAttribute("Authorization");
        //清空token
        JWTUtil.invalidateToken(token);
        return new ResultTUtil().success("注销成功");
    }

    @ApiOperation("获取用户列表")
    @PostMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public RowsTUtil<YunUserVo> listUser(@Validated GetUserListParam pageInfo, @Validated PageInfoParam param) {
        System.out.println(pageInfo);
        System.out.println(param);
        // 开启分页
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        //后台列表根据多个条件查询-todo 增加请求参数与qw条件过滤
        QueryWrapper<YunUser> yunUserVoQueryWrapper = new QueryWrapper<>();
        yunUserVoQueryWrapper.like(pageInfo.getAccount() != null, "account", pageInfo.getAccount());
        yunUserVoQueryWrapper.like(pageInfo.getUserName() != null, "user_name", pageInfo.getUserName());
        List<YunUser> list = yunUserService.list(yunUserVoQueryWrapper);
        System.out.println(list);
        // 获取分页信息
        PageInfo<YunUser> pageList = new PageInfo<>(list);
        //转化脱敏
        // 转换为VO列表
        ArrayList<YunUserVo> yunUserVos = new ArrayList<>();
        for (YunUser yunUser : list) {
            YunUserVo vo = new YunUserVo();
            BeanUtil.copyProperties(yunUser, vo);
            yunUserVos.add(vo);
        }
        return new RowsTUtil<YunUserVo>().success("查询成功", pageList.getTotal(), yunUserVos);
    }

    @ApiOperation("创建用户")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil addUser(@Validated AddYunUserParam param) {
        //校验空参数
        if (StringUtils.isAllBlank(param.getAccount(), param.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号和密码不能为空!");
        }
        YunUser yunUser = new YunUser();
        BeanUtils.copyProperties(param, yunUser);
        //密码加密存储
        //加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + param.getPassword()).getBytes());
        yunUser.setPassword(newPassword);
        yunUser.setCreateBy(param.getAccount());
        yunUser.setUpdateBy(param.getAccount());
        yunUser.setCreateTime(new Date());
        yunUser.setUpdateTime(new Date());
        boolean save = yunUserService.save(yunUser);
        if (save) {
            return new ResultTUtil<>().success("创建用户成功");
        } else {
            return new ResultTUtil<>().error("创建用户成功");
        }
    }

    @ApiOperation("查询用户详情/未脱敏")
    @GetMapping("/detail")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil<YunUser> detailUser(@RequestParam Long userId) {
        //校验参数
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id不能为空!");
        }
        YunUser byId = yunUserService.getById(userId);
        return new ResultTUtil<YunUser>().success("查询成功", byId);
    }

    @ApiOperation("修改用户")
    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil updateUser(@Validated UpdateYunUserParam param) {
        //校验空参数
        if (StringUtils.isAllBlank(param.getAccount(), param.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号和密码不能为空!");
        }
        //校验空参数
        if (param.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id不能为空!");
        }
        YunUser yunUser = new YunUser();
        BeanUtils.copyProperties(param, yunUser);
        //密码加密存储
        //加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + param.getPassword()).getBytes());
        yunUser.setPassword(newPassword);
        yunUser.setUpdateBy(param.getAccount());
        yunUser.setUpdateTime(new Date());
        boolean save = yunUserService.updateById(yunUser);
        if (save) {
            return new ResultTUtil<>().success("修改用户成功");
        } else {
            return new ResultTUtil<>().error("修改用户成功");
        }
    }

    @ApiOperation("查询用户详情/脱敏")
    @GetMapping("/detail/vo")
    public ResultTUtil<YunUserVo> detailUserVo(@RequestParam Long userId) {
        YunUserVo yunUserVo = new YunUserVo();
        //校验参数
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id不能为空!");
        }
        YunUser byId = yunUserService.getById(userId);
        //脱敏
        BeanUtils.copyProperties(byId, yunUserVo);
        return new ResultTUtil<YunUserVo>().success("查询成功", yunUserVo);
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{userId}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil delUser(@PathVariable Long userId) {
        //校验参数
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id不能为空!");
        }
        boolean b = yunUserService.removeById(userId);
        if (b) {
            return new ResultTUtil<>().success("删除成功");
        } else {
            return new ResultTUtil<>().error("删除成功");
        }
    }

}

