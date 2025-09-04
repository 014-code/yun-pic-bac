package com.mashang.yunbac.web.entity.params.picture;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchPicParam {

    /**
     * 图片ids
     */
    @Schema(description = "图片ids")
    private List<Long> picIds;
    /**
     * 空间id
     */
    @Schema(description = "空间id")
    private Long spaceId;
    /**
     * 标签
     */
    @Schema(description = "标签")
    private List<String> tags;
    /**
     * 分类
     */
    @Schema(description = "分类")
    private String category;
    /**
     * 命名规则
     */
    @Schema(description = "命名规则")
    private String nameRole;

}
