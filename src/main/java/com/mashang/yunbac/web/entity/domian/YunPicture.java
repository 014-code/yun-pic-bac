package com.mashang.yunbac.web.entity.domian;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * (YunPicture)实体类
 *
 * @author makejava
 * @since 2025-08-19 20:51:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("yun_picture")
public class YunPicture {
    /**
     * 图片id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long picId;
    /**
     * 图片地址
     */
    private String url;
    /**
     * 图片名称
     */
    private String name;
    /**
     * 简介
     */
    private String introduction;
    /**
     * 分类
     */
    private String category;
    /**
     * 标签-json
     */
    private String tags;
    /**
     * 图片体积
     */
    private Long picSize;
    /**
     * 图片宽度
     */
    private Integer picWidth;
    /**
     * 图片高度
     */
    private Integer picHeight;
    /**
     * 图片宽高比
     */
    private Double picScale;
    /**
     * 图片格式
     */
    private String picFormat;
    /**
     * 空间id
     */
    private Long spaceId;
    /**
     * 图片状态
     */
    private String status;
    /**
     * 原因
     */
    private String reason;
    /**
     * 创建用户
     */
    private Long userId;
    /**
     * 缩略图url
     */
    private String thumbnailUrl;
    /**
     * 审核人id
     */
    private Long reviewId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;

}

