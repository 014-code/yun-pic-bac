package com.mashang.yunbac.web.api.imagesearch.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ImageSearchVo {

    /**
     * 图片地址
     */
    private String imgUrl;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片key
     */
    private String imgkey;

    /**
     * HTTP
     */
    private String http;

    /**
     * HTTPS
     */
    private String https;

}
