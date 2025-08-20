package com.mashang.yunbac.web.entity.params.picture;

import io.swagger.annotations.ApiModelProperty;
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
public class PictrueParam {
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

}

