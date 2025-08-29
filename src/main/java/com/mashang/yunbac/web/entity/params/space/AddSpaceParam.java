package com.mashang.yunbac.web.entity.params.space;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * (YunUser)实体类
 *
 * @author makejava
 * @since 2025-08-18 11:03:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddSpaceParam {
    //空间名称
    @Schema(description = "空间名称", required = false)
    private String spaceName;
    //空间级别()0普通1专业2旗舰)
    @Schema(description = "空间级别(0普通1专业2旗舰)", required = true)
    private Long spaceLevel;
    //最大总大小s
    @Schema(description = "最大总大小s", required = false)
    private BigDecimal maxSize;
    //空间图片最大总数量
    @Schema(description = "空间图片最大总数量", required = false)
    private Long maxCount;
    //用户id
    @Schema(description = "用户id", required = false)
    private Long userId;

}

