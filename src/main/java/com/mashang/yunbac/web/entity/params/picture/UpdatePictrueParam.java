package com.mashang.yunbac.web.entity.params.picture;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class UpdatePictrueParam {
    /**
     * 图片名称
     */
    @Schema(description = "图片id")
    private Long picId;
    /**
     * 图片名称
     */
    @Schema(description = "图片名称", required = false)
    private String name;
    /**
     * 简介
     */
    @Schema(description = "简介", required = false)
    private String introduction;
    /**
     * 分类
     */
    @Schema(description = "分类", required = false)
    private String category;
    /**
     * 标签-json
     */
    @Schema(description = "标签-json", required = false)
    private List<String> tags;

}

