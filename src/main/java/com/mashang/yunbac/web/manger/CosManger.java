package com.mashang.yunbac.web.manger;

import com.mashang.yunbac.web.config.CosConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

/**
 * cos公共操作方法
 */
@Service
public class CosManger {

    @Resource
    private CosConfig cosConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传任意对象
     *
     * @param key
     * @param file
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        //新建cos上传对象
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载任意对象
     *
     * @param key
     * @return
     */
    public COSObject getObj(String key) {
        //新建cos下载对象
        GetObjectRequest putObjectRequest = new GetObjectRequest(cosConfig.getBucket(), key);
        return cosClient.getObject(putObjectRequest);
    }

    /**
     * 上传对象-调用数据万象-附带图片信息
     *
     * @param key
     * @return
     */
    public PutObjectResult putPictrueObj(String key, File file) {
        //新建cos上传对象
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucket(), key, file);
        PicOperations picOperations = new PicOperations();
        //1表示返回原图信息
        picOperations.setIsPicInfo(1);
        //处理
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }


}
