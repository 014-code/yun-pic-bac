package com.mashang.yunbac.web.manger;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import com.mashang.yunbac.web.config.CosConfig;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.vo.picture.UploadPictureResult;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.exception.ThrowUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    private static final List<String> ALLOWED_FORMATS = Arrays.asList("jpeg", "jpg", "png", "webp");
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024L; // 2MB
    @Autowired
    private CosManger cosManger;

    /**
     * 上传图片主方法
     *
     * @param multipartFile    上传的文件
     * @param uploadPathPrefix 云存储路径前缀
     * @return 上传结果封装
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        // 1. 参数校验
        validPicture(multipartFile);

        // 2. 生成唯一文件名
        String uuid = UUID.randomUUID().toString().substring(0, 16);
        String originFilename = multipartFile.getOriginalFilename();
        String fileExtension = FileUtil.getSuffix(originFilename);
        String uploadFilename = String.format("%s_%s.%s",
                DateUtil.formatDate(new Date()),
                uuid,
                fileExtension);
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFilename);

        File tempFile = null;
        try {
            // 3. 创建临时文件
            tempFile = File.createTempFile("upload_", ".tmp");
            multipartFile.transferTo(tempFile);

            // 4. 上传到云存储
            PutObjectResult putObjectResult = cosManger.putPictrueObj(uploadPath, tempFile);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();

            // 5. 封装返回结果
            UploadPictureResult result = new UploadPictureResult();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

            result.setPicName(FileUtil.mainName(originFilename));
            result.setPicWidth(picWidth);
            result.setPicHeight(picHeight);
            result.setPicScale(picScale);
            result.setPicFormat(imageInfo.getFormat());
            result.setPicSize(FileUtil.size(tempFile));
            result.setUrl(cosConfig.getHost() + "/" + uploadPath);

            return result;

        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            deleteTempFile(tempFile);
        }
    }

    /**
     * 校验文件
     */
    public void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");

        // 文件大小校验
        long fileSize = multipartFile.getSize();
        ThrowUtils.throwIf(fileSize > MAX_FILE_SIZE, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");

        // 文件后缀校验
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        ThrowUtils.throwIf(!ALLOWED_FORMATS.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }

    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

}
