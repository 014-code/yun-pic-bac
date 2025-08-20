package com.mashang.yunbac.web.entity.params.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源评论列表查询请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RegisterParam {

    @ApiModelProperty(value = "账号", required = true)
    private String account;
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    @ApiModelProperty(value = "再次输入密码", required = true)
    private String rePassword;
    @ApiModelProperty(value = "用户名称", required = false)
    private String userName;

}