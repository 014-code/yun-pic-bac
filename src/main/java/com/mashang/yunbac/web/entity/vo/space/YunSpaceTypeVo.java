package com.mashang.yunbac.web.entity.vo.space;

import com.mashang.yunbac.web.entity.domian.YunSpace;
import com.mashang.yunbac.web.entity.domian.YunUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YunSpaceTypeVo {
    /**
     * 空间级别id
     */
    @Schema(description = "空间级别id")
    private Long code;
    /**
     * 空间级别名称
     */
    @Schema(description = "空间级别名称")
    private String type;
    /**
     * 最大总大小mb
     */
    @Schema(description = "最大总大小mb")
    private BigDecimal maxSize;
    /**
     * 空间图片最大总数量
     */
    @Schema(description = "空间图片最大总数量")
    private Long maxCount;
}
