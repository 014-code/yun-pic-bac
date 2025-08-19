package com.mashang.yunbac.web.manger.file;

import com.mashang.yunbac.web.config.CosConfig;
import com.qcloud.cos.COSClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 通用文件上传服务
 */
@Service
@Slf4j
public class FileManger {

    @Resource
    private CosConfig cosConfig;

    @Resource
    private COSClient cosClient;



}
