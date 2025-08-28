package com.mashang.yunbac.web.controller;

import com.mashang.yunbac.web.annotation.AuthCheck;
import com.mashang.yunbac.web.constant.UserConstant;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.manger.CosManger;
import com.mashang.yunbac.web.utils.ResultTUtil;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file")
@Tag(name = "文件模块")
public class FlieController {

    @Resource
    private CosManger cosManger;

    @Operation(summary = "测试文件上传")
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil<String> upload(@RequestPart("file") MultipartFile file) {
        //拿到文件名称和路径
        String originalFilename = file.getOriginalFilename();
        String path = String.format("/test/%s", originalFilename);
        //空文件
        File files = null;
        try {
            //创建空文件模板
            files = File.createTempFile(path, null);
            //把MultipartFile类型转化成File
            file.transferTo(files);
            //通用上传
            cosManger.putObject(path, files);
            //返回图片地址
            return new ResultTUtil().success(path);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            if (files != null) {
                //删除临时文件
                boolean delete = files.delete();
                if (!delete) {
                    log.error("删除临时文件失败");
                }
            }
        }
    }

    @Operation(summary = "测试文件下载")
    @PostMapping("/download")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void download(String filePath, HttpServletRequest request, HttpServletResponse response) {
        //创建cos输入流对象
        COSObjectInputStream cosObjectInputStream = null;
        try {
            //调用通用下载
            COSObject obj = cosManger.getObj(filePath);
            //下载后的流对象
            cosObjectInputStream = obj.getObjectContent();
            //处理成字节流
            byte[] byteArray = IOUtils.toByteArray(cosObjectInputStream);
            //设置像一头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("content-Disposition", "attachment; filename=" + filePath);
            //写入响应头
            response.getOutputStream().write(byteArray);
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件下载失败");
        } finally {
            try {
                if (cosObjectInputStream != null) {
                    cosObjectInputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
