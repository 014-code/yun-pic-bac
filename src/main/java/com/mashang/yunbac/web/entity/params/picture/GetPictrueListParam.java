package com.mashang.yunbac.web.entity.params.picture;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * (YunUser)实体类
 *
 * @author makejava
 * @since 2025-08-18 11:03:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPictrueListParam {
    /**
     * 图片名称
     */
    @ApiModelProperty("图片名称")
    private String name;
    /**
     * 图片格式
     */
    @ApiModelProperty("图片格式")
    private String picFormat;
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
     * 搜索词
     */
    @ApiModelProperty("搜索词(同时搜索名称简介)")
    private String seacherText;

}

