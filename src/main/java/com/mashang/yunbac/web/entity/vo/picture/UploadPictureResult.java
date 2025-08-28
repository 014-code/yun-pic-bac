package com.mashang.yunbac.web.entity.vo.picture;


import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "图片访问URL")
    private String url;

    /**
     * 原始图片文件名
     */
    @Schema(description = "原始图片文件名")
    private String picName;

    /**
     * 文件体积
     */
    @Schema(description = "文件体积")
    private Long picSize;

    /**
     * 图片像素宽度
     */
    @Schema(description = "图片像素宽度")
    private int picWidth;

    /**
     * 图片像素高度
     */
    @Schema(description = "图片像素高度")
    private int picHeight;

    /**
     * 宽高比（宽度/高度）
     */
    @Schema(description = "宽高比（宽度/高度）")
    private Double picScale;

    /**
     * 图片格式
     */
    @Schema(description = "图片格式")
    private String picFormat;

    /**
     * 缩略图地址
     */
    @Schema(description = "缩略图地址")
    private String thumbnailUrl;

}
