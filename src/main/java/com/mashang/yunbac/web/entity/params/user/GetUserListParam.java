package com.mashang.yunbac.web.entity.params.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GetUserListParam {

    @Schema(description = "账号")
    private String account;
    @Schema(description = "用户名")
    private String userName;

}
