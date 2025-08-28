package com.mashang.yunbac.web.entity.params.picture;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "图片地址")
    private String url;
    /**
     * 图片名称
     */
    @Schema(description = "图片名称")
    private String name;
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

}

