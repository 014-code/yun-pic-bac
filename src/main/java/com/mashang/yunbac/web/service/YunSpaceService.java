package com.mashang.yunbac.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mashang.yunbac.web.entity.domian.YunSpace;
import com.mashang.yunbac.web.entity.params.picture.BatchPicParam;
import com.mashang.yunbac.web.entity.params.space.AddSpaceParam;
import com.mashang.yunbac.web.utils.ResultTUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * (YunSpace)表服务接口
 *
 * @author makejava
 * @since 2025-08-29 12:27:49
 */
public interface YunSpaceService extends IService<YunSpace> {

    ResultTUtil addSpace(AddSpaceParam param, HttpServletRequest request);

    ResultTUtil batch(BatchPicParam batchPicParam, HttpServletRequest request);
}

