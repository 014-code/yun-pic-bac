package com.mashang.yunbac.web.entity.vo.picture;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * (YunPicture)实体类
 *
 * @author makejava
 * @since 2025-08-19 20:51:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YunPictureVo {
    /**
     * 图片id
     */
    @Schema(description = "图片id")
    private Long picId;
    /**
     * 图片地址
     */
    @Schema(description = "图片地址")
    private String url;
    /**
     * 图片名称
     */
    @Schema(description = "图片名称")
    private String name;
    /**
     * 简介
     */
    @Schema(description = "简介")
    private String introduction;
    /**
     * 分类
     */
    @Schema(description = "分类")
    private String category;
    /**
     * 标签-json
     */
    @Schema(description = "标签-json")
    private List<String> tags;
    /**
     * 图片体积
     */
    @Schema(description = "图片体积")
    private Long picSize;
    /**
     * 图片宽度
     */
    @Schema(description = "图片宽度")
    private Integer picWidth;
    /**
     * 图片高度
     */
    @Schema(description = "图片高度")
    private Integer picHeight;
    /**
     * 图片宽高比
     */
    @Schema(description = "图片宽高比")
    private String picScale;
    /**
     * 图片格式
     */
    @Schema(description = "图片格式")
    private String picFormat;
    /**
     * 图片状态
     */
    @Schema(description = "图片状态")
    private String status;
    /**
     * 缩略图url
     */
    @Schema(description = "缩略图url")
    private String thumbnailUrl;
    /**
     * 原因
     */
    @Schema(description = "原因")
    private String reason;
    /**
     * 创建用户
     */
    @Schema(description = "创建用户")
    private Long userId;
    /**
     * 审核人id
     */
    @Schema(description = "审核人id")
    private Long reviewId;
    /**
     * 图片主色调
     */
    @Schema(description = "图片主色调")
    private String picColor;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}

