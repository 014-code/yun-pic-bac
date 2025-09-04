package com.mashang.yunbac.web.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mashang.yunbac.web.annotation.AuthCheck;
import com.mashang.yunbac.web.constant.UserConstant;
import com.mashang.yunbac.web.entity.domian.YunPicture;
import com.mashang.yunbac.web.entity.domian.YunSpace;
import com.mashang.yunbac.web.entity.domian.YunSpace;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.enums.PicStatusEnum;
import com.mashang.yunbac.web.entity.enums.SpaceEnum;
import com.mashang.yunbac.web.entity.params.common.PageInfoParam;
import com.mashang.yunbac.web.entity.params.picture.BatchPicParam;
import com.mashang.yunbac.web.entity.params.picture.UpdatePictrueParam;
import com.mashang.yunbac.web.entity.params.space.AddSpaceParam;
import com.mashang.yunbac.web.entity.params.space.GetSpaceListParam;
import com.mashang.yunbac.web.entity.params.space.UpdateSpaceParam;
import com.mashang.yunbac.web.entity.vo.space.YunSpaceTypeVo;
import com.mashang.yunbac.web.entity.vo.space.YunSpaceUserVo;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.exception.ThrowUtils;
import com.mashang.yunbac.web.service.YunSpaceService;
import com.mashang.yunbac.web.service.impl.YunPictureServiceImpl;
import com.mashang.yunbac.web.service.impl.YunUserServiceImpl;
import com.mashang.yunbac.web.utils.JWTUtil;
import com.mashang.yunbac.web.utils.ResultTUtil;
import com.mashang.yunbac.web.utils.RowsTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * (YunSpace)表控制层
 *
 * @author makejava
 * @since 2025-08-29 12:27:47
 */
@Slf4j
@RestController
@RequestMapping("/yunSpace")
@Tag(name = "空间模块")
public class YunSpaceController {
    /**
     * 服务对象
     */
    @Resource
    private YunSpaceService yunSpaceService;
    @Autowired
    private YunUserServiceImpl yunUserService;
    @Autowired
    private YunPictureServiceImpl yunPictureService;

    @Operation(summary = "创建空间")
    @PostMapping("/add")
    public ResultTUtil addSpace(@Validated @RequestBody AddSpaceParam param, HttpServletRequest request) {
        return yunSpaceService.addSpace(param, request);
    }

    @Operation(summary = "查询空间列表-管理员")
    @PostMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public RowsTUtil<YunSpace> list(@RequestBody @Validated GetSpaceListParam getSpaceListParam) {
        // 开启分页
        PageHelper.startPage(getSpaceListParam.getPageNum(), getSpaceListParam.getPageSize());
        //后台列表根据多个条件查询
        QueryWrapper<YunSpace> yunUserVoQueryWrapper = new QueryWrapper<>();
        yunUserVoQueryWrapper.like(getSpaceListParam.getSpaceName() != null, "space_name", getSpaceListParam.getSpaceName());
        yunUserVoQueryWrapper.like(getSpaceListParam.getSpaceLevel() != null, "space_level", getSpaceListParam.getSpaceLevel());
        List<YunSpace> list = yunSpaceService.list(yunUserVoQueryWrapper);
        //空间级别转化
        list.stream().forEach(yunSpace -> {
            yunSpace.setSpaceLevel(Objects.requireNonNull(SpaceEnum.getEnumByValue(Long.valueOf(yunSpace.getSpaceLevel()))).getType());
        });
        // 获取分页信息
        PageInfo<YunSpace> pageList = new PageInfo<>(list);
        return new RowsTUtil<YunSpace>().success("查询成功", pageList.getTotal(), pageList.getList());
    }

    @Operation(summary = "查询空间详情")
    @GetMapping("/detail")
    public ResultTUtil<YunSpaceUserVo> detail(@RequestParam Long spaceId) {
        ThrowUtils.throwIf(spaceId == null, ErrorCode.NOT_FOUND_ERROR);
        YunSpace byId = yunSpaceService.getById(spaceId);
        ThrowUtils.throwIf(byId == null, ErrorCode.NOT_FOUND_ERROR);
        YunSpaceUserVo yunSpaceUserVo = new YunSpaceUserVo();
        //空间级别转化
        byId.setSpaceLevel(Objects.requireNonNull(SpaceEnum.getEnumByValue(Long.valueOf(byId.getSpaceLevel()))).getType());
        yunSpaceUserVo.setYunSpace(byId);
        //用户信息
        YunUser yunUser = yunUserService.getById(byId.getUserId());
        yunSpaceUserVo.setYunUser(yunUser);
        return new ResultTUtil<YunSpaceUserVo>().success("查询成功", yunSpaceUserVo);
    }

    @Operation(summary = "查询所有空间类别")
    @GetMapping("/type")
    public ResultTUtil<List<YunSpaceTypeVo>> type() {
        List<YunSpaceTypeVo> yunSpaceTypeVos = SpaceEnum.convertToSpaceTypeVoList();
        return new ResultTUtil<List<YunSpaceTypeVo>>().success("查询成功", yunSpaceTypeVos);
    }

    @Operation(summary = "更新空间信息-管理员")
    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil update(@RequestBody @Validated UpdateSpaceParam updateSpaceParam) {
        ThrowUtils.throwIf(updateSpaceParam.getSpaceId() == null, ErrorCode.NOT_FOUND_ERROR);
        //判断空间类型是否存在
        ThrowUtils.throwIf(!SpaceEnum.getValues().contains(updateSpaceParam.getSpaceLevel()), ErrorCode.NOT_FOUND_ERROR);
        YunSpace yunSpace = new YunSpace();
        BeanUtils.copyProperties(updateSpaceParam, yunSpace);
        //设置对应类型空间大小
        yunSpace.setMaxSize(SpaceEnum.getEnumByValue(Long.valueOf(updateSpaceParam.getSpaceLevel())).getMaxSize());
        yunSpace.setMaxCount(SpaceEnum.getEnumByValue(Long.valueOf(updateSpaceParam.getSpaceLevel())).getMaxCount());
        boolean b = yunSpaceService.updateById(yunSpace);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);
        return new ResultTUtil<>().success("修改成功");
    }

    @Operation(summary = "删除空间")
    @DeleteMapping("/del/{spaceId}")
    public ResultTUtil del(@PathVariable Long spaceId) {
        YunSpace byId = yunSpaceService.getById(spaceId);
        ThrowUtils.throwIf(byId == null, ErrorCode.NOT_FOUND_ERROR);
        boolean b = yunSpaceService.removeById(byId);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);
        //删除空间内的所有图片
        QueryWrapper<YunPicture> yunPictureQueryWrapper = new QueryWrapper<>();
        yunPictureQueryWrapper.eq("space_id", byId.getSpaceId());
        yunPictureService.remove(yunPictureQueryWrapper);
        return new ResultTUtil<>().success("删除成功");
    }

    @Operation(summary = "查询当前用户所有空间")
    @GetMapping("/all/space")
    public ResultTUtil<List<YunSpace>> allSpace(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = JWTUtil.getUserId(token);
        QueryWrapper<YunSpace> queryWrapper = new QueryWrapper<YunSpace>().eq("user_id", userId);
        List<YunSpace> list = yunSpaceService.list(queryWrapper);
        return new ResultTUtil<List<YunSpace>>().success("查询成功", list);
    }

    @Operation(summary = "图片批量管理")
    @PutMapping("/batch")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ResultTUtil batch(@RequestBody @Validated BatchPicParam batchPicParam, HttpServletRequest request) {
        return yunSpaceService.batch(batchPicParam, request);
    }


}

