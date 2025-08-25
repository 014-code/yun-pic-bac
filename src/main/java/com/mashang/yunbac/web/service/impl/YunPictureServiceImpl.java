package com.mashang.yunbac.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.enums.UserRoleEnum;
import com.mashang.yunbac.web.entity.params.picture.CaptureParam;
import com.mashang.yunbac.web.entity.vo.picture.UploadPictureResult;
import com.mashang.yunbac.web.entity.vo.picture.YunPictureVo;
import com.mashang.yunbac.web.entity.vo.user.YunUserVo;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.exception.ThrowUtils;
import com.mashang.yunbac.web.manger.FileManger;
import com.mashang.yunbac.web.mapper.YunPictureMapper;
import com.mashang.yunbac.web.service.YunPictureService;
import com.mashang.yunbac.web.entity.domian.YunPicture;
import com.mashang.yunbac.web.utils.ResultTUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 上传图片
     *
     * @param file
     * @param picId
     * @param yunUser
     * @return
     */
    @Override
    public ResultTUtil<YunPictureVo> uploadPic(Object file, Long picId, YunUser yunUser, String picName) {
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
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        //区分上传的是文件类型还是url
        if (file instanceof MultipartFile) {
            //把从filemanger得到的图片信息入库-更新或者新增
            uploadPictureResult = fileManger.uploadPicture((MultipartFile) file, format);
        } else if (file instanceof String) {
            //把从filemanger得到的图片信息入库-更新或者新增
            uploadPictureResult = fileManger.uploadPicture((String) file, format, false);
        }

        YunPicture yunPicture = new YunPicture();
        yunPicture.setUserId(yunUser.getUserId());
        //如果有指定名称
        if (picName != null) {
            yunPicture.setName(picName);
        } else {
            yunPicture.setName(uploadPictureResult.getPicName());
        }
        yunPicture.setIntroduction("该图片暂无简介");
        yunPicture.setPicSize(uploadPictureResult.getPicSize());
        yunPicture.setPicWidth(uploadPictureResult.getPicWidth());
        yunPicture.setPicHeight(uploadPictureResult.getPicHeight());
        yunPicture.setPicScale(uploadPictureResult.getPicScale());
        yunPicture.setPicFormat(uploadPictureResult.getPicFormat());
        yunPicture.setCreateTime(new Date());
        yunPicture.setUpdateTime(new Date());
        yunPicture.setUrl(uploadPictureResult.getUrl());
        //为管理员则直接通过
        if (yunUser.getRole() != null && yunUser.getRole().equals(UserRoleEnum.ADMIN.getValue())) {
            yunPicture.setStatus("1");
        }
        //是否为更新
        if (picId != null) {
            yunPicture.setPicId(picId);
            yunPictureMapper.updateById(yunPicture);
        } else {
            yunPictureMapper.insert(yunPicture);
            yunPicture.setPicId(yunPictureMapper.selectById(yunPicture.getPicId()).getPicId());
        }
        YunPictureVo yunPictureVo = new YunPictureVo();
        BeanUtils.copyProperties(yunPicture, yunPictureVo);
        yunPictureVo.setPicId(yunPicture.getPicId());
        return new ResultTUtil<YunPictureVo>().success("查询成功", yunPictureVo);
    }

    /**
     * 批量抓取图片
     *
     * @param captureParam
     * @return
     */
    @Override
    public ResultTUtil capture(CaptureParam captureParam, YunUser yunUserVo) {
        Long num = captureParam.getNum();
        String text = captureParam.getText();
        String prefix = captureParam.getPrefix();
        //拿到抓取地址
        String format = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", text);
        Document document;
        try {
            //抓取对应地址
            document = Jsoup.connect(format).get();
        } catch (IOException e) {
            log.error(String.valueOf(e));
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取页面失败");
        }
        //拿到带有这个类名dom元素
        Element dgControl = document.getElementsByClass("dgControl").first();
        if (dgControl == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取元素失败");
        }
        //css选择器
        Elements select = dgControl.select(".iuscp.isv");
        Long uploadCount = 1L;
        Long successCount = 0L;
        Long failCount = 0L;
        
        for (Element element : select) {
            try {
                String src = element.select(".iusc").get(0).attr("m");
                //转义地址-?所在的下标
                Pattern pattern = Pattern.compile("\"murl\":\"(.*?)\"");
                Matcher matcher = pattern.matcher(src);
                if (matcher.find()) {
                    src = matcher.group(1);
                }
                if (src == null || src.equals("")) {
                    log.error(String.valueOf(element));
                    continue;
                }
                int i = src.indexOf("?");
                if (i != -1) {
                    //切割地址
                    src = src.substring(0, i);
                }
                String pciName = null;
                //名称默认为搜索词
                if (prefix == null) {
                    pciName = text;
                } else {
                    pciName = prefix;
                }
                //调用url上传，跳过验证避免网络问题
                uploadPic(src, null, yunUserVo, pciName + uploadCount);
                uploadCount++;
                successCount++;
                //数量达到则跳出
                if (uploadCount > num) {
                    break;
                }
            } catch (Exception e) {
                failCount++;
//                log.warn("处理图片失败，跳过: {}, 错误: {}", element, e.getMessage());
                continue; // 继续处理下一个
            }
        }
        
        String resultMsg = String.format("操作完成，成功: %d, 失败: %d", successCount, failCount);
        return new ResultTUtil().success(resultMsg);
    }
}
