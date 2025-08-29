package com.mashang.yunbac.web.entity.vo.space;

import com.mashang.yunbac.web.entity.domian.YunSpace;
import com.mashang.yunbac.web.entity.domian.YunUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YunSpaceUserVo {
    /**
     * 空间信息
     */
    @Schema(description = "空间信息")
    private YunSpace yunSpace;
    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private YunUser yunUser;
}
