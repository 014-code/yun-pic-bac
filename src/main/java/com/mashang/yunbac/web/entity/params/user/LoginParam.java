package com.mashang.yunbac.web.entity.params.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源评论列表查询请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoginParam")
public class LoginParam {

    @Schema(description = "账号", required = true)
    private String account;
    @Schema(description = "密码", required = true)
    private String password;

}