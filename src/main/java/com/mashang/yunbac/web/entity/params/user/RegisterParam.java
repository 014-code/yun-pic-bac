package com.mashang.yunbac.web.entity.params.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RegisterParam")
public class RegisterParam {

    @Schema(description = "账号", required = true)
    private String account;
    @Schema(description = "密码", required = true)
    private String password;
    @Schema(description = "再次输入密码", required = true)
    private String rePassword;
    @Schema(description = "用户名称")
    private String userName;

}