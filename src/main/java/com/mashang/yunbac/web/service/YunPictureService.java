package com.mashang.yunbac.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mashang.yunbac.web.entity.domian.YunPicture;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.params.common.PageInfoParam;
import com.mashang.yunbac.web.entity.params.picture.CaptureParam;
import com.mashang.yunbac.web.entity.params.picture.GetPictrueListParam;
import com.mashang.yunbac.web.entity.vo.picture.YunPictureVo;
import com.mashang.yunbac.web.entity.vo.user.YunUserVo;
import com.mashang.yunbac.web.utils.ResultTUtil;
import com.mashang.yunbac.web.utils.RowsTUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * (YunPicture)表服务接口
 *
 * @author makejava
 * @since 2025-08-19 20:51:55
 */
public interface YunPictureService extends IService<YunPicture> {


    ResultTUtil<YunPictureVo> uploadPic(Object file, Long picId, YunUser yunUser, String picName);

    ResultTUtil capture(CaptureParam captureParam, YunUser yunUserVo);

    RowsTUtil<YunPictureVo> listVo(PageInfoParam pageInfoParam, GetPictrueListParam getPictrueListParam);

    void clearPicFile(YunPicture yunPicture);
}
