package com.mashang.yunbac.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mashang.yunbac.web.entity.domian.YunPicture;
import com.mashang.yunbac.web.entity.domian.YunSpace;
import com.mashang.yunbac.web.entity.domian.YunUser;
import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.entity.enums.SpaceEnum;
import com.mashang.yunbac.web.entity.params.picture.BatchPicParam;
import com.mashang.yunbac.web.entity.params.space.AddSpaceParam;
import com.mashang.yunbac.web.exception.BusinessException;
import com.mashang.yunbac.web.exception.ThrowUtils;
import com.mashang.yunbac.web.mapper.YunPictureMapper;
import com.mashang.yunbac.web.mapper.YunSpaceMapper;
import com.mashang.yunbac.web.mapper.YunUserMapper;
import com.mashang.yunbac.web.service.YunPictureService;
import com.mashang.yunbac.web.service.YunSpaceService;
import com.mashang.yunbac.web.utils.JWTUtil;
import com.mashang.yunbac.web.utils.ResultTUtil;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * (YunSpace)表服务实现类
 *
 * @author makejava
 * @since 2025-08-29 12:27:49
 */
@Service("yunSpaceService")
public class YunSpaceServiceImpl extends ServiceImpl<YunSpaceMapper, YunSpace> implements YunSpaceService {

    @Resource
    private YunUserMapper yunUserMapper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Autowired
    private YunSpaceMapper yunSpaceMapper;
    @Autowired
    private YunPictureMapper yunPictureMapper;

    private YunPictureService yunPictureService;

    @Lazy
    @Autowired
    public void setYunPictureService(YunPictureService yunPictureService) {
        this.yunPictureService = yunPictureService;
    }

    private final ThreadPoolExecutor commonThreadPool;

    @Autowired
    public YunSpaceServiceImpl(
            @Lazy YunPictureService yunPictureService,
            @Qualifier("commonThreadPool") ThreadPoolExecutor commonThreadPool) {
        this.yunPictureService = yunPictureService;
        this.commonThreadPool = commonThreadPool;
    }


    @Override
    public ResultTUtil addSpace(AddSpaceParam param, HttpServletRequest request) {
        //校验参数
        ThrowUtils.throwIf(param.getSpaceLevel() == null, ErrorCode.PARAMS_ERROR, "空间类型不能为空");
        //判断用户创建的空间类别是哪种-用户只能普通
        //拿到当前登录用户
        String token = request.getHeader("Authorization");
        Long userId = JWTUtil.getUserId(token);
        YunUser yunUser = yunUserMapper.selectById(userId);
        //查看要创建的空间id是否存在
        if (!SpaceEnum.getValues().contains(param.getSpaceLevel())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无此空间");
        }
        //如果不是普通空间
        if (!param.getSpaceLevel().equals(1) && !yunUser.getRole().equals("admin")) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建该空间");
        }
        // 针对用户加锁
        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            //事务编程
            Long execute = transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery().eq(YunSpace::getUserId, userId).exists();
                //用户是管理员则跳过
                if (!yunUser.getRole().equals("admin")) {
                    ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有空间");
                }
                param.setUserId(userId);
                //设置对应类型空间大小
                param.setMaxSize(SpaceEnum.getEnumByValue(param.getSpaceLevel()).getMaxSize());
                param.setMaxCount(SpaceEnum.getEnumByValue(param.getSpaceLevel()).getMaxCount());
                YunSpace yunSpace = new YunSpace();
                BeanUtils.copyProperties(param, yunSpace);
                yunSpace.setSpaceLevel(String.valueOf(param.getSpaceLevel()));
                //插值
                boolean save = this.save(yunSpace);
                ThrowUtils.throwIf(save, ErrorCode.OPERATION_ERROR);
                return yunSpace.getSpaceId();
            });
            return new ResultTUtil().success("创建成功");
        }
    }

    /**
     * 批量编辑图片
     *
     * @param batchPicParam
     * @return
     */
//    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultTUtil batchO(BatchPicParam batchPicParam, HttpServletRequest request) {
        ThrowUtils.throwIf(batchPicParam.getSpaceId() == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        ThrowUtils.throwIf(batchPicParam.getPicIds().isEmpty(), ErrorCode.NOT_FOUND_ERROR, "图片id不能为空");
        //校验空间权限
        YunSpace yunSpace = yunSpaceMapper.selectById(batchPicParam.getSpaceId());
        ThrowUtils.throwIf(yunSpace == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        //用户信息
        String authorization = request.getHeader("Authorization");
        Long userId = JWTUtil.getUserId(authorization);
        ThrowUtils.throwIf(!userId.equals(yunSpace.getUserId()), ErrorCode.NO_AUTH_ERROR, "无该空间的权限");
        //查询指定图片
        QueryWrapper<YunPicture> yunPictureQueryWrapper = new QueryWrapper<>();
        yunPictureQueryWrapper.eq("space_id", batchPicParam.getSpaceId()).in("pic_id", batchPicParam.getPicIds());
        List<YunPicture> yunPictures = yunPictureMapper.selectList(yunPictureQueryWrapper);
        if (yunPictures.isEmpty()) {
            return new ResultTUtil().error("修改成功");
        }
        yunPictures.forEach(yunPicture -> {
            yunPicture.setTags(JSONUtil.toJsonStr(yunPicture.getTags()));
            yunPicture.setCategory(yunPicture.getCategory());
        });
        boolean b = yunPictureService.updateBatchById(yunPictures);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return new ResultTUtil().success("修改成功");
    }

    /**
     * 批量编辑图片分类和标签-线程池 + 分批处理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultTUtil batch(BatchPicParam batchPicParam, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(batchPicParam.getSpaceId() == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        ThrowUtils.throwIf(batchPicParam.getPicIds().isEmpty(), ErrorCode.NOT_FOUND_ERROR, "图片id不能为空");
        //校验空间权限
        YunSpace yunSpace = yunSpaceMapper.selectById(batchPicParam.getSpaceId());
        ThrowUtils.throwIf(yunSpace == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        //用户信息
        String authorization = request.getHeader("Authorization");
        Long userId = JWTUtil.getUserId(authorization);
        ThrowUtils.throwIf(!userId.equals(yunSpace.getUserId()), ErrorCode.NO_AUTH_ERROR, "无该空间的权限");
        // 查询空间下的图片
        //查询指定图片
        QueryWrapper<YunPicture> yunPictureQueryWrapper = new QueryWrapper<>();
        yunPictureQueryWrapper.eq("space_id", batchPicParam.getSpaceId()).in("pic_id", batchPicParam.getPicIds());
        List<YunPicture> pictureList = yunPictureMapper.selectList(yunPictureQueryWrapper);

        if (pictureList.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "指定的图片不存在或不属于该空间");
        }

        // 分批处理避免长事务
        try {
            int batchSize = 100;
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 0; i < pictureList.size(); i += batchSize) {
                final List<YunPicture> batch = pictureList.subList(i, Math.min(i + batchSize, pictureList.size()));

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    // 1. 更新分类和标签
                    batch.forEach(picture -> {
                        if (batchPicParam.getCategory() != null) {
                            picture.setCategory(batchPicParam.getCategory());
                        }
                        if (batchPicParam.getTags() != null) {
                            picture.setTags(String.join(",", batchPicParam.getTags()));
                        }
                    });

                    // 2. 批量重命名
                    if (StrUtil.isNotBlank(batchPicParam.getNameRole())) {
                        fillNameRole(batch, batchPicParam.getNameRole());
                    }

                    // 3. 执行批量更新
                    boolean success = yunPictureService.updateBatchById(batch);
                    if (!success) {
                        throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量更新失败");
                    }
                }, commonThreadPool).exceptionally(e -> {
                    log.error("批量处理异常", e);
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "处理失败: " + e.getMessage());
                });

                futures.add(future);
            }

            // 等待所有任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return new ResultTUtil().success("批量更新成功");

        } catch (Exception e) {
            log.error("批量处理主流程异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
        }
    }

    /**
     * 批量重命名方法
     *
     */
    private void fillNameRole(List<YunPicture> pictureList, String nameRule) {
        // 参数校验
        if (CollUtil.isEmpty(pictureList) || StrUtil.isBlank(nameRule)) {
            return;
        }
        // 检查命名规则是否包含序号占位符
        if (!nameRule.contains("{序号}")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "命名规则必须包含'{序号}'占位符");
        }
        long count = 1;
        try {
            for (YunPicture picture : pictureList) {
                // 替换占位符生成图片名称
                String pictureName = nameRule.replaceAll("\\{序号}", String.valueOf(count++));
                picture.setName(pictureName);
            }
        } catch (Exception e) {
            log.error("名称解析错误", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "名称解析错误");
        }
    }
}

