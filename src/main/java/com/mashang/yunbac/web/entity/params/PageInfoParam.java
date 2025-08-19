package com.mashang.yunbac.web.entity.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 分页对象实体类
 */
@Data
public class PageInfoParam {

    @ApiModelProperty(value = "条数", required = true)
    @NotNull("pageSize不能为空!")
    private Integer pageSize;
    @ApiModelProperty(value = "页码", required = true)
    @NotNull("pageNum不能为空!")
    private Integer pageNum;

}
