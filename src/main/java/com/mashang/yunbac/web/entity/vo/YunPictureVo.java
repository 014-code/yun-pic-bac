package com.mashang.yunbac.web.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
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
@TableName("yun_picture")
public class YunPictureVo {
    /**
     * 图片id
     */
    @ApiModelProperty("图片id")
    private Long picId;
    /**
     * 图片地址
     */
    @ApiModelProperty("图片地址")
    private String url;
    /**
     * 图片名称
     */
    @ApiModelProperty("图片名称")
    private String name;
    /**
     * 简介
     */
    @ApiModelProperty("简介")
    private String introduction;
    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private String category;
    /**
     * 标签-json
     */
    @ApiModelProperty("标签-json")
    private List<String> tags;
    /**
     * 图片体积
     */
    @ApiModelProperty("图片体积")
    private Long picSize;
    /**
     * 图片宽度
     */
    @ApiModelProperty("图片宽度")
    private Integer picWidth;
    /**
     * 图片高度
     */
    @ApiModelProperty("图片高度")
    private Integer picHeight;
    /**
     * 图片宽高比
     */
    @ApiModelProperty("图片宽高比")
    private String picScale;
    /**
     * 图片格式
     */
    @ApiModelProperty("图片格式")
    private String picFormat;
    /**
     * 创建用户
     */
    @ApiModelProperty("创建用户")
    private Long userId;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}

