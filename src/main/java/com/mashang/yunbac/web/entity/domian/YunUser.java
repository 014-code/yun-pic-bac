package com.mashang.yunbac.web.entity.domian;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (YunUser)实体类
 *
 * @author makejava
 * @since 2025-08-18 11:03:25
 */
@TableName(value = "yun_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YunUser {
    /**
     * 用户id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;
    /**
     * 用户名称
     */
    private String userName;
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
    private String role;
    /**
     * 简介
     */
    private String profile;
    /**
     * 删除标志(0存在2删除)
     */
    @TableLogic
    private String delFlag;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 创建者
     */
    private String createBy;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    /**
     * 更新者
     */
    private String updateBy;

}

