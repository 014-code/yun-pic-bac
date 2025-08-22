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
public class ReviewPicParam {
    /**
     * 图片名称
     */
    @ApiModelProperty(value = "图片id", required = true)
    private Long picId;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态", required = true)
    private String status;
    /**
     * 状态
     */
    @ApiModelProperty(value = "原因", required = false)
    private String reason;

}

