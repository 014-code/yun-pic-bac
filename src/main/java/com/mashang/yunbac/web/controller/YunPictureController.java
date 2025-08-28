package com.mashang.yunbac.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mashang.yunbac.web.annotation.AuthCheck;
import com.mashang.yunbac.web.constant.UserConstant;
import com.mashang.yunbac.web.entity.domian.YunPicture;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.enums.PicStatusEnum;
import com.mashang.yunbac.web.entity.params.common.PageInfoParam;
import com.mashang.yunbac.web.entity.params.picture.CaptureParam;
import com.mashang.yunbac.web.entity.params.picture.GetPictrueListParam;
import com.mashang.yunbac.web.entity.params.picture.ReviewPicParam;
import com.mashang.yunbac.web.entity.params.picture.UpdatePictrueParam;
import com.mashang.yunbac.web.entity.vo.common.YunCategoryTagVo;
import com.mashang.yunbac.web.entity.vo.picture.YunPictureUserVo;
import com.mashang.yunbac.web.entity.vo.picture.YunPictureUserVos;
import com.mashang.yunbac.web.entity.vo.picture.YunPictureVo;
import com.mashang.yunbac.web.entity.vo.user.YunUserVo;
import com.mashang.yunbac.web.exception.ThrowUtils;
import com.mashang.yunbac.web.service.impl.YunUserServiceImpl;
import com.mashang.yunbac.web.utils.JWTUtil;
import com.mashang.yunbac.web.utils.ResultTUtil;
import com.mashang.yunbac.web.utils.RowsTUtil;
import com.mashang.yunbac.web.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.mashang.yunbac.web.service.YunPictureService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * (YunPicture)表控制层
 *
 * @author makejava
 * @since 2025-08-19 20:51:52
 */
@Slf4j
@RestController
@RequestMapping("/yunPicture")
@Tag(name = "图片模块")
public class YunPictureController {
    /**
     * 服务对象
     */
    @Resource
    private YunPictureService yunPictureService;
    @Autowired
    private YunUserServiceImpl yunUserService;

    @Operation(summary = "上传图片(并返回图片信息)")
    @PostMapping("/uploadPic")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil<YunPictureVo> uploadPic(MultipartFile file, Long picId, HttpServletRequest request) {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录或缺少token");
        }

        // 验证token并获取用户ID
        boolean valid = JWTUtil.verifyToken(token);
        if (!valid) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户认证失败");
        }

        Long userId = JWTUtil.getUserId(token);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在或token过期");
        }

        // 从数据库获取完整的用户信息
        YunUser yunUser = yunUserService.getById(userId);
        if (yunUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在");
        }
        return yunPictureService.uploadPic(file, picId, yunUser, null);
    }

    @Operation(summary = "url上传图片(并返回图片信息)")
    @PostMapping("/uploadPic/url")
//    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public ResultTUtil<YunPictureVo> uploadPicUrl(String file, Long picId, HttpServletRequest request) {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录或缺少token");
        }

        // 验证token并获取用户ID
        boolean valid = JWTUtil.verifyToken(token);
        if (!valid) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户认证失败");
        }

        Long userId = JWTUtil.getUserId(token);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在或token过期");
        }

        // 从数据库获取完整的用户信息
        YunUser yunUser = yunUserService.getById(userId);
        if (yunUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在");
        }

        return yunPictureService.uploadPic(file, picId, yunUser, null);
    }

    @Operation(summary = "分页查询图片列表-管理员")
    @PostMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public RowsTUtil<YunPicture> list(@Validated PageInfoParam pageInfoParam, @Validated GetPictrueListParam getPictrueListParam) {
        // 开启分页
        PageHelper.startPage(pageInfoParam.getPageNum(), pageInfoParam.getPageSize());
        //后台列表根据多个条件查询
        QueryWrapper<YunPicture> yunUserVoQueryWrapper = new QueryWrapper<>();
        yunUserVoQueryWrapper.like(getPictrueListParam.getCategory() != null, "category", getPictrueListParam.getCategory());
        yunUserVoQueryWrapper.like(getPictrueListParam.getName() != null, "name", getPictrueListParam.getName());
        yunUserVoQueryWrapper.like(getPictrueListParam.getIntroduction() != null, "introduction", getPictrueListParam.getIntroduction());
        yunUserVoQueryWrapper.eq(getPictrueListParam.getPicFormat() != null, "pic_format", getPictrueListParam.getPicFormat());
        yunUserVoQueryWrapper.eq(getPictrueListParam.getPicSize() != null, "pic_size", getPictrueListParam.getPicSize());
        yunUserVoQueryWrapper.eq(getPictrueListParam.getPicWidth() != null, "pic_width", getPictrueListParam.getPicWidth());
        yunUserVoQueryWrapper.eq(getPictrueListParam.getPicHeight() != null, "pic_height", getPictrueListParam.getPicHeight());
        yunUserVoQueryWrapper.eq(getPictrueListParam.getPicScale() != null, "pic_scale", getPictrueListParam.getPicScale());
        //json数组查询
        if (CollUtil.isNotEmpty(getPictrueListParam.getTags())) {
            for (String tag : getPictrueListParam.getTags()) {
                yunUserVoQueryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        List<YunPicture> list = yunPictureService.list(yunUserVoQueryWrapper);
        //状态转化
        list.stream().forEach(yunPicture -> {
            yunPicture.setStatus(PicStatusEnum.getEnumByValue(yunPicture.getStatus()).getValue());
        });
        // 获取分页信息
        PageInfo<YunPicture> pageList = new PageInfo<>(list);
        return new RowsTUtil<YunPicture>().success("查询成功", pageList.getTotal(), pageList.getList());
    }

    @Operation(summary = "查询图片详情-管理员")
    @GetMapping("/detail")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil<YunPictureUserVo> detail(Long picId) {
        YunPictureUserVo yunPictureUserVo = new YunPictureUserVo();
        YunPicture byId = yunPictureService.getById(picId);
        ThrowUtils.throwIf(byId == null, ErrorCode.NOT_FOUND_ERROR);
        //关联用户信息
        YunUser user = yunUserService.getById(byId.getUserId());
        yunPictureUserVo.setYunPicture(byId);
        yunPictureUserVo.setYunUser(user);
        return new ResultTUtil<YunPictureUserVo>().success("查询成功", yunPictureUserVo);
    }

    @Operation(summary = "更新图片信息-管理员")
    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil update(@RequestBody @Validated UpdatePictrueParam updatePictrueParam) {
        ThrowUtils.throwIf(updatePictrueParam.getPicId() == null, ErrorCode.NOT_FOUND_ERROR);
        YunPicture yunPicture = new YunPicture();
        BeanUtils.copyProperties(updatePictrueParam, yunPicture);
        String jsonStr = JSONUtil.toJsonStr(updatePictrueParam.getTags());
        yunPicture.setTags(jsonStr);
        boolean b = yunPictureService.updateById(yunPicture);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);
        return new ResultTUtil<>().success("修改成功");
    }

    @Operation(summary = "删除图片-管理员")
    @DeleteMapping("/del/{picId}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil del(@PathVariable Long picId) {
        YunPicture byId = yunPictureService.getById(picId);
        ThrowUtils.throwIf(byId == null, ErrorCode.NOT_FOUND_ERROR);
        boolean b = yunPictureService.removeById(byId);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);
        return new ResultTUtil<>().success("删除成功");
    }

    @Operation(summary = "分页获取图片")
    @PostMapping("/list/vo")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public RowsTUtil<YunPictureVo> listVo(@Validated PageInfoParam pageInfoParam, @Validated GetPictrueListParam getPictrueListParam) {
        return yunPictureService.listVo(pageInfoParam, getPictrueListParam);
    }

    @Operation(summary = "查询图片详情")
    @GetMapping("/detail/vo")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public ResultTUtil<YunPictureUserVos> detailVo(Long picId) {
        YunPictureUserVos yunPictureUserVo = new YunPictureUserVos();
        YunPicture byId = yunPictureService.getById(picId);
        ThrowUtils.throwIf(byId == null, ErrorCode.NOT_FOUND_ERROR);
        //关联用户信息
        YunUser user = yunUserService.getById(byId.getUserId());
        YunPictureVo yunPictureVo = new YunPictureVo();
        List<String> list = JSONUtil.parseArray(byId.getTags()).toList(String.class);
        BeanUtils.copyProperties(byId, yunPictureVo);
        yunPictureVo.setTags(list);
        YunUserVo yunUserVo = new YunUserVo();
        if (user != null) {
            BeanUtils.copyProperties(user, yunUserVo);
        }
        yunPictureUserVo.setYunPicture(yunPictureVo);
        yunPictureUserVo.setYunUser(yunUserVo);
        return new ResultTUtil<YunPictureUserVos>().success("查询成功", yunPictureUserVo);
    }

    @Operation(summary = "修改图片")
    @PutMapping("/update/vo")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public ResultTUtil updateVo(@RequestBody @Validated UpdatePictrueParam updatePictrueParam) {
        ThrowUtils.throwIf(updatePictrueParam.getPicId() == null, ErrorCode.NOT_FOUND_ERROR);
        YunPicture yunPicture = new YunPicture();
        BeanUtils.copyProperties(updatePictrueParam, yunPicture);
        String jsonStr = JSONUtil.toJsonStr(updatePictrueParam.getTags());
        yunPicture.setTags(jsonStr);
        //重置为待审核
        yunPicture.setStatus("0");
        boolean b = yunPictureService.updateById(yunPicture);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);
        return new ResultTUtil<>().success("修改成功");
    }

    @Operation(summary = "查询所有标签和类别")
    @GetMapping("/tags/all")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public ResultTUtil<YunCategoryTagVo> allTags() {
        YunCategoryTagVo yunCategoryTagVo = new YunCategoryTagVo();
        List<String> tags = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> cor = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        yunCategoryTagVo.setTags(tags);
        yunCategoryTagVo.setCategory(cor);
        return new ResultTUtil<YunCategoryTagVo>().success("查询成功", yunCategoryTagVo);
    }

    @Operation(summary = "审核图片")
    @PutMapping("/review")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public ResultTUtil review(@RequestBody @Validated ReviewPicParam reviewPicParam, HttpServletRequest request) {
        // 从头部获取我们的token
        String token = request.getHeader("Authorization");
        //校验
        ThrowUtils.throwIf(reviewPicParam.getPicId() == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(reviewPicParam.getStatus() == null, ErrorCode.NOT_FOUND_ERROR);
        //更新状态
        YunPicture yunPicture = new YunPicture();
        yunPicture.setPicId(reviewPicParam.getPicId());
        yunPicture.setStatus(reviewPicParam.getStatus());
        yunPicture.setReason(reviewPicParam.getReason());
        yunPicture.setReviewId(JWTUtil.getUserId(token));
        boolean b = yunPictureService.updateById(yunPicture);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);
        return new ResultTUtil().success("审核成功");
    }

    @Operation(summary = "批量抓取图片")
    @PostMapping("/capture")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil capture(@RequestBody @Validated CaptureParam captureParam) {
        //校验参数
        ThrowUtils.throwIf(captureParam == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(captureParam.getNum() == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(captureParam.getText() == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(captureParam.getYunUser() == null, ErrorCode.NOT_FOUND_ERROR);
        //防爬虫-最多50条
        ThrowUtils.throwIf(captureParam.getNum() > 50, ErrorCode.NOT_FOUND_ERROR, "最大数量不能超过50条");
        return yunPictureService.capture(captureParam, captureParam.getYunUser());
    }

}

