package com.mashang.yunbac.web.entity.vo.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YunCategoryTagVo {

    /**
     * 分类信息
     */
    @ApiModelProperty("分类信息")
    private List<String> category;

    /**
     * 标签信息
     */
    @ApiModelProperty("标签信息")
    private List<String> tags;


}
