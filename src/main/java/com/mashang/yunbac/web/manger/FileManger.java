package com.mashang.yunbac.web.manger;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.mashang.yunbac.web.config.CosConfig;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.vo.picture.UploadPictureResult;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.exception.ThrowUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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

    //上传图片允许的图片类型
    private static final List<String> ALLOWED_FORMATS = Arrays.asList("jpeg", "jpg", "png", "webp");
    //url请求允许的图片类型
    private static final List<String> ALLOWED = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
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
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, fileExtension);
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
     * url上传图片主方法
     *
     * @param multipartFile    上传的文件路径
     * @param uploadPathPrefix 云存储路径前缀
     * @return 上传结果封装
     */
    public UploadPictureResult uploadPicture(String multipartFile, String uploadPathPrefix) {
        return uploadPicture(multipartFile, uploadPathPrefix, false);
    }

    /**
     * url上传图片主方法（支持跳过验证）
     *
     * @param multipartFile    上传的文件路径
     * @param uploadPathPrefix 云存储路径前缀
     * @param skipValidation   是否跳过URL验证
     * @return 上传结果封装
     */
    public UploadPictureResult uploadPicture(String multipartFile, String uploadPathPrefix, boolean skipValidation) {
        // 1. 参数校验
        validPicture(multipartFile, skipValidation);

        // 2. 生成唯一文件名
        String uuid = UUID.randomUUID().toString().substring(0, 16);
//        String originFilename = multipartFile.getOriginalFilename();
        //通过url获取文件名称
        String fileExtension = FileUtil.mainName(multipartFile);
        //转化上传后的文件名称
        String substring = "temp_" + UUID.randomUUID().toString().substring(0, 8);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, fileExtension);
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFilename);

        File tempFile = null;
        try {
            // 3. 创建临时文件
            tempFile = File.createTempFile(substring, null);
//            multipartFile.transferTo(tempFile);
            //请求下载
            HttpUtil.downloadFile(multipartFile, tempFile);
            //
            // 4. 上传到云存储
            PutObjectResult putObjectResult = cosManger.putPictrueObj(uploadPath, tempFile);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();

            // 5. 封装返回结果
            UploadPictureResult result = new UploadPictureResult();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

            result.setPicName(FileUtil.mainName(fileExtension));
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
     * 校验文件
     */
    public void validPicture(String multipartFile) {
        validPicture(multipartFile, false);
    }

    /**
     * 校验文件
     * @param multipartFile 文件URL
     * @param skipValidation 是否跳过验证（用于批量操作）
     */
    public void validPicture(String multipartFile, boolean skipValidation) {
        if (skipValidation) {
            log.info("跳过URL验证: {}", multipartFile);
            return;
        }
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件地址不能为空");

        //校验url是否合法
        try {
            new URL(multipartFile);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件地址格式不正确");
        }

        //校验url协议是否为http
        ThrowUtils.throwIf(!(multipartFile.startsWith("http://") || multipartFile.startsWith("https://")), ErrorCode.PARAMS_ERROR, "仅支持http和https的地址");

        //创建请求
        try {
            HttpResponse execute = HttpUtil.createRequest(Method.HEAD, multipartFile).execute();
            //请求地址不存在则直接返回
            if (execute.getStatus() != HttpStatus.SC_OK) {
                return;
            }
            //校验文件类型
            String header = execute.header("Content-Type");
            if (StrUtil.isNotBlank(header)) {
                ThrowUtils.throwIf(!(ALLOWED.contains(header.toLowerCase())), ErrorCode.PARAMS_ERROR, "不支持该文件类型");
            }
            //校验文件大小
            String length = execute.header("Content-Length");
            if (StrUtil.isNotBlank(length)) {
                try {
                    long l = Long.parseLong(length);
                    ThrowUtils.throwIf(l > MAX_FILE_SIZE, ErrorCode.PARAMS_ERROR, "文件大小不能超过2MB");
                } catch (NumberFormatException e) {
                    log.warn("无法获取文件大小，跳过大小校验: {}", length);
                }
            }
            //关闭
            execute.close();
        } catch (Exception e) {
            // 网络验证失败时，记录日志但跳过验证，继续处理
            log.warn("验证图片URL失败，跳过验证继续处理: {}, 错误: {}", multipartFile, e.getMessage());
        }
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
