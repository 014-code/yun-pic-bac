package com.mashang.yunbac.web.entity.params.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetUserListParam {

    @ApiModelProperty(value = "账号", required = false)
    private String account;
    @ApiModelProperty(value = "用户名", required = false)
    private String userName;

}
