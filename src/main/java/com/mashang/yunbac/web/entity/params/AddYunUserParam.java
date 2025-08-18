package com.mashang.yunbac.web.entity.params;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * (YunUser)实体类
 *
 * @author makejava
 * @since 2025-08-18 11:03:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddYunUserParam {
    /**
     * 用户名称
     */
    private String userName = "无名氏";
    /**
     * 账号
     */
    @NotNull("账号不能为空")
    private String account;
    /**
     * 密码
     */
    @NotNull("密码不能为空")
    private String password;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 角色(user/admin)
     */
    private String role = "user";
    /**
     * 简介
     */
    private String profile = "这个用户很懒，没写简介";

}

