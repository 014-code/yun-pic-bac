package com.mashang.yunbac.web.entity.params.space;

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
public class GetSpaceListParam {
    /**
     * 图片名称
     */
    @Schema(description = "空间名称", required = false)
    private String spaceName;
    /**
     * 图片格式
     */
    @Schema(description = "空间级别", required = false)
    private Long spaceLevel;

}

