package com.mashang.yunbac.web.manger;

import cn.hutool.core.io.FileUtil;
import com.mashang.yunbac.web.config.CosConfig;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.exception.BusinessException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * cos公共操作方法
 */
@Service
@Slf4j
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
     * 上传对象-调用数据万象-附带图片信息-包括压缩操作
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
        List<PicOperations.Rule> rules = new ArrayList<>();
        //压缩转成webp格式-webpKey
        String webpKey = FileUtil.mainName(key) + ".webp";
        //新建转化参数-后续对图片修改等操作都是通过此类构造请求
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setRule("imageMogr2/format/webp");
        compressRule.setBucket(cosConfig.getBucket());
        compressRule.setFileId(webpKey);
        rules.add(compressRule);
        //大于20kb才进行缩略，避免无效消耗
        if (file.length() > 2 * 1024) {
            //缩略图key
            String tubKey = FileUtil.mainName(key) + "._thumbnail" + FileUtil.getSuffix(key);
            //缩略图处理
            PicOperations.Rule tubRule = new PicOperations.Rule();
            tubRule.setBucket(cosConfig.getBucket());
            tubRule.setFileId(tubKey);
            //定义缩放规则
            tubRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 128, 128));
            rules.add(tubRule);
        }

        //处理
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        
        log.info("开始上传图片到COS，key: {}, fileSize: {}", key, file.length());
        PutObjectResult result = cosClient.putObject(putObjectRequest);
        log.info("COS上传完成，result: {}", result);
        log.info("CI上传结果: {}", result.getCiUploadResult());
        
        return result;
    }

    /**
     * 删除对象
     *
     * @param key
     */
    public void delObj(String key) {
        try {
            cosClient.deleteObject(cosConfig.getBucket(), key);
        } catch (CosClientException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除对象失败");
        }
    }


}
