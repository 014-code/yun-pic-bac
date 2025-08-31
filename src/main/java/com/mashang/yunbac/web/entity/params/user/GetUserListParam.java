package com.mashang.yunbac.web.entity.params.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserListParam {

    @Schema(description = "账号")
    private String account;
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "条数", required = true)
    @NotNull("pageSize不能为空!")
    private Integer pageSize;
    @Schema(description = "页码", required = true)
    @NotNull("pageNum不能为空!")
    private Integer pageNum;

}
