package com.mashang.yunbac.web.entity.params;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传通用请求
 */
@Data
public class PictureUploadParam implements Serializable {

    private Long picId;

    private static final long serialVersionUID = 1L;

}
