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
public class UpdatePictrueParam {
    /**
     * 图片名称
     */
    @ApiModelProperty("图片id")
    private Long picId;
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
    private List<String> category;
    /**
     * 标签-json
     */
    @ApiModelProperty("标签-json")
    private List<String> tags;

}

