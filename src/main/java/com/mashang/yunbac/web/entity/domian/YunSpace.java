package com.mashang.yunbac.web.entity.domian;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (YunSpace)表实体类
 *
 * @author makejava
 * @since 2025-08-29 12:27:48
 */
@TableName(value = "yun_space")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YunSpace {
    //空间id
    @TableId(type = IdType.ASSIGN_ID)
    private Long spaceId;
    //空间名称
    private String spaceName;
    //空间级别()0普通1专业2旗舰)
    private String spaceLevel;
    //最大总大小
    private BigDecimal maxSize;
    //空间图片最大总数量
    private Long maxCount;
    //当前空间下图片的总大小
    private BigDecimal totalSize;
    //当前空间下的图片数量
    private Long totalCount;
    //用户id
    private Long userId;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    //删除标志
    @TableLogic
    private String delFlag;

}

