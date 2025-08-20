package com.mashang.yunbac.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.vo.picture.UploadPictureResult;
import com.mashang.yunbac.web.entity.vo.picture.YunPictureVo;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.exception.ThrowUtils;
import com.mashang.yunbac.web.manger.FileManger;
import com.mashang.yunbac.web.mapper.YunPictureMapper;
import com.mashang.yunbac.web.service.YunPictureService;
import com.mashang.yunbac.web.entity.domian.YunPicture;
import com.mashang.yunbac.web.utils.ResultTUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;

/**
 * (YunPicture)表服务实现类
 *
 * @author makejava
 * @since 2025-08-19 20:51:56
 */
@Service("yunPictureService")
public class YunPictureServiceImpl extends ServiceImpl<YunPictureMapper, YunPicture> implements YunPictureService {

    @Resource
    private YunPictureMapper yunPictureMapper;
    @Autowired
    private FileManger fileManger;

    @Override
    public ResultTUtil<YunPictureVo> uploadPic(MultipartFile file, Long picId, YunUser yunUser) {
        //判断传入的用户信息是否有登录，没则直接抛异常
        ThrowUtils.throwIf(yunUser == null, ErrorCode.NOT_LOGIN_ERROR);
        //判断是新增图片还是更新图片-有id就是更新,如果是更新则需要校验图片是否存在，不存在也要抛异常
        if (picId != null) {
            //库表查是否存在
            QueryWrapper<YunPicture> yunPictureQueryWrapper = new QueryWrapper<>();
            yunPictureQueryWrapper.eq("pic_id", picId).eq("user_id", yunUser.getUserId());
            YunPicture yunPicture = yunPictureMapper.selectOne(yunPictureQueryWrapper);
            ThrowUtils.throwIf(yunPicture == null, ErrorCode.NOT_FOUND_ERROR);
        }
        //使用filemanger得上传图片，还得划分目录，这里划分至public目录即可 + 用户id
        String format = "public/" + yunUser.getUserId();
        //把从filemanger得到的图片信息入库-更新或者新增
        UploadPictureResult uploadPictureResult = fileManger.uploadPicture(file, format);
        YunPicture yunPicture = new YunPicture();
        yunPicture.setUserId(yunUser.getUserId());
        yunPicture.setName(uploadPictureResult.getPicName());
        yunPicture.setIntroduction("该图片暂无简介");
        yunPicture.setPicSize(uploadPictureResult.getPicSize());
        yunPicture.setPicWidth(uploadPictureResult.getPicWidth());
        yunPicture.setPicHeight(uploadPictureResult.getPicHeight());
        yunPicture.setPicScale(uploadPictureResult.getPicScale());
        yunPicture.setPicFormat(uploadPictureResult.getPicFormat());
        yunPicture.setCreateTime(new Date());
        yunPicture.setUpdateTime(new Date());
        //是否为更新
        if (picId != null) {
            yunPicture.setPicId(picId);
        }
        boolean b = yunPictureMapper.insertOrUpdate(yunPicture);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        YunPictureVo yunPictureVo = new YunPictureVo();
        BeanUtils.copyProperties(yunPicture, yunPictureVo);
        return new ResultTUtil<YunPictureVo>().success("查询成功", yunPictureVo);
    }
}
