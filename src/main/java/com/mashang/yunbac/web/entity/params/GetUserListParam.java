package com.mashang.yunbac.web.entity.params;

import com.github.pagehelper.page.PageParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
public class GetUserListParam {

    @ApiModelProperty(value = "账号", required = false)
    private String account;
    @ApiModelProperty(value = "用户名", required = false)
    private String userName;

}
