package com.mashang.yunbac.web.entity.vo.user;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "脱敏用户信息vo类")
public class YunUserVo {
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long userId;
    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;
    /**
     * 账号
     */
    @Schema(description = "账号")
    private String account;
    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;
    /**
     * 角色(user/admin)
     */
    @Schema(description = "角色")
    private String role;
    /**
     * 简介
     */
    @Schema(description = "简介")
    private String profile;
    /**
     * 删除标志(0存在2删除)
     */
    @TableLogic
    @Schema(description = "删除标志")
    private String delFlag;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updateBy;

}

