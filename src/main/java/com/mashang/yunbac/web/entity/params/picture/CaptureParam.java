package com.mashang.yunbac.web.entity.params.picture;

import com.mashang.yunbac.web.entity.domian.YunUser;
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
public class CaptureParam {
    /**
     * 图片名称
     */
    @ApiModelProperty("搜索词")
    private String text;
    /**
     * 图片格式
     */
    @ApiModelProperty("抓取数量")
    private Long num = 10l;
    /**
     * 简介
     */
    @ApiModelProperty("图片前缀(名称)")
    private String prefix;

    /**
     * 用户
     */
    @ApiModelProperty("用户")
    private YunUser yunUser;

}

