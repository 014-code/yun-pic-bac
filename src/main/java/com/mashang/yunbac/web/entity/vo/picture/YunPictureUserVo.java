package com.mashang.yunbac.web.entity.vo.picture;

import com.mashang.yunbac.web.entity.domian.YunPicture;
import com.mashang.yunbac.web.entity.domian.YunUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YunPictureUserVo {

    /**
     * 图片信息
     */
    @ApiModelProperty("图片信息")
    private YunPicture yunPicture;

    /**
     * 用户信息
     */
    @ApiModelProperty("用户信息")
    private YunUser yunUser;


}
