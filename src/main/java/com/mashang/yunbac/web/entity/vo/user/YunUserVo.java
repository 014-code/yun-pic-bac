package com.mashang.yunbac.web.entity.vo.user;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("脱敏用户信息vo类")
public class YunUserVo {
    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long userId;
    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    private String userName;
    /**
     * 账号
     */
    @ApiModelProperty("账号")
    private String account;
    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;
    /**
     * 角色(user/admin)
     */
    @ApiModelProperty("角色")
    private String role;
    /**
     * 简介
     */
    @ApiModelProperty("简介")
    private String profile;
    /**
     * 删除标志(0存在2删除)
     */
    @TableLogic
    @ApiModelProperty("删除标志")
    private String delFlag;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String createBy;
    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updateBy;

}

