package com.mashang.yunbac.web.entity.vo.picture;

import com.mashang.yunbac.web.entity.vo.user.YunUserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YunPictureUserVos {

    /**
     * 图片信息
     */
    @ApiModelProperty("图片信息")
    private YunPictureVo yunPicture;

    /**
     * 用户信息
     */
    @ApiModelProperty("用户信息")
    private YunUserVo yunUser;


}
