package com.mashang.yunbac.web.entity.params.picture;

import com.mashang.yunbac.web.entity.params.common.PageInfoParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * (YunUser)实体类
 *
 * @author makejava
 * @since 2025-08-18 11:03:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPictrueListParam {
    /**
     * 图片名称
     */
    @Schema(description = "图片名称", required = false)
    private String name;
    /**
     * 图片格式
     */
    @Schema(description = "图片格式", required = false)
    private String picFormat;
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
    /**
     * 图片体积
     */
    @Schema(description = "图片体积", required = false)
    private Long picSize;
    /**
     * 图片宽度
     */
    @Schema(description = "图片宽度", required = false)
    private Integer picWidth;
    /**
     * 图片高度
     */
    @Schema(description = "图片高度", required = false)
    private Integer picHeight;
    /**
     * 图片宽高比
     */
    @Schema(description = "图片宽高比", required = false)
    private String picScale;
    /**
     * 搜索词
     */
    @Schema(description = "搜索词(同时搜索名称简介)", required = false)
    private String seacherText;
    /**
     * 空间id
     */
    @Schema(description = "空间id", required = false)
    private Long spaceId;
    /**
     * 搜索词
     */
    @Schema(description = "是否只查询spaceId为null的数据(是就查公共图库，不是就查对应的)", required = false)
    private Boolean nullSpace;

    @Schema(description = "条数", required = true)
    @NotNull("pageSize不能为空!")
    private Integer pageSize;
    @Schema(description = "页码", required = true)
    @NotNull("pageNum不能为空!")
    private Integer pageNum;
}

