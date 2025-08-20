package com.mashang.yunbac.web.entity.vo.picture;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadPictureResult {

    /**
     * 图片访问URL
     */
    @ApiModelProperty("图片访问URL")
    private String url;

    /**
     * 原始图片文件名
     */
    @ApiModelProperty("原始图片文件名")
    private String picName;

    /**
     * 文件体积
     */
    @ApiModelProperty("文件体积")
    private Long picSize;

    /**
     * 图片像素宽度
     */
    @ApiModelProperty("图片像素宽度")
    private int picWidth;

    /**
     * 图片像素高度
     */
    @ApiModelProperty("图片像素高度")
    private int picHeight;

    /**
     * 宽高比（宽度/高度）
     */
    @ApiModelProperty("宽高比（宽度/高度）")
    private Double picScale;

    /**
     * 图片格式
     */
    @ApiModelProperty("图片格式")
    private String picFormat;

}
