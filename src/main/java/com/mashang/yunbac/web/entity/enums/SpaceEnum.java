package com.mashang.yunbac.web.entity.enums;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.mashang.yunbac.web.entity.vo.space.YunSpaceTypeVo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 空间类型枚举
 */
public enum SpaceEnum {

    ORDINARY(0L, "普通空间", BigDecimal.valueOf(512), 50L),
    MAJOR(1L, "专业空间", BigDecimal.valueOf(1024 * 5), 100L),
    FLAGSHIP(2L, "旗舰空间", BigDecimal.valueOf(1024 * 10), 500L);

    /**
     * 状态码
     */
    private final Long code;

    /**
     * 信息
     */
    private final String type;

    /**
     * 最大总大小mb
     */
    private final BigDecimal maxSize;

    /**
     * 空间图片最大总数量
     */
    private final Long maxCount;

    SpaceEnum(Long code, String type, BigDecimal maxSize, Long maxCount) {
        this.code = code;
        this.type = type;
        this.maxSize = maxSize;
        this.maxCount = maxCount;
    }

    public Long getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getMaxSize() {
        return maxSize;
    }
    public Long getMaxCount() {
        return maxCount;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Long> getValues() {
        return Arrays.stream(values()).map(item -> item.code).collect(Collectors.toList());
    }

    /**
     * 根据 code 获取枚举
     *
     * @param code
     * @return
     */
    public static SpaceEnum getEnumByValue(Long code) {
        if (ObjectUtils.isEmpty(code)) {
            return null;
        }
        for (SpaceEnum anEnum : SpaceEnum.values()) {
            if (anEnum.code.equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 将 SpaceEnum 的所有枚举值转换为 YunSpaceTypeVo 列表
     *
     * @return List<YunSpaceTypeVo>
     */
    public static List<YunSpaceTypeVo> convertToSpaceTypeVoList() {
        return Arrays.stream(SpaceEnum.values())
                .map(enumItem -> new YunSpaceTypeVo(
                        enumItem.getCode(),      // Long 类型，对应 code
                        enumItem.getType(),       // String 类型，对应空间级别名称
                        enumItem.getMaxSize(),
                        enumItem.getMaxCount()
                ))
                .collect(Collectors.toList());
    }

}
