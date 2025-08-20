package com.mashang.yunbac.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mashang.yunbac.web.entity.domian.YunPicture;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.vo.picture.YunPictureVo;
import com.mashang.yunbac.web.utils.ResultTUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * (YunPicture)表服务接口
 *
 * @author makejava
 * @since 2025-08-19 20:51:55
 */
public interface YunPictureService extends IService<YunPicture> {


    ResultTUtil<YunPictureVo> uploadPic(MultipartFile file, Long picId, YunUser yunUser);
}
