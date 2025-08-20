package com.mashang.yunbac.web.entity.params.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (YunUser)实体类
 *
 * @author makejava
 * @since 2025-08-18 11:03:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateYunUserParam {

    /**
     * 用户id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;
    /**
     * 用户名称
     */
    private String userName = "无名氏";
    /**
     * 账号
     */
    private String account;
    /**
     * 密码
     */
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

