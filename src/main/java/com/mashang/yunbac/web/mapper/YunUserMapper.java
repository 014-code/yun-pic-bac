package com.mashang.yunbac.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mashang.yunbac.web.entity.domian.YunUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (YunUser)表数据库访问层
 *
 * @author makejava
 * @since 2025-08-18 11:03:25
 */
@Mapper
public interface YunUserMapper extends BaseMapper<YunUser> {


}

