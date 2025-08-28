package com.mashang.yunbac.web.entity.params.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 分页对象实体类
 */
@Data
public class PageInfoParam {

    @Schema(description = "条数", required = true)
    @NotNull("pageSize不能为空!")
    private Integer pageSize;
    @Schema(description = "页码", required = true)
    @NotNull("pageNum不能为空!")
    private Integer pageNum;

}
