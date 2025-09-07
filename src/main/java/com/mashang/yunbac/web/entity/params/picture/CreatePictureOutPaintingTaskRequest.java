package com.mashang.yunbac.web.entity.params.picture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private Parameters parameters;

    private static final long serialVersionUID = 1L;

    @Data
    public static class Parameters {
        /**
         * 水平扩展比例，默认为1.0，范围[1.0, 3.0]
         */
        private Float xScale;

        /**
         * 垂直扩展比例，默认为1.0，范围[1.0, 3.0]
         */
        private Float yScale;

        /**
         * 上方添加像素数，默认为0
         */
        private Integer topOffset;

        /**
         * 下方添加像素数，默认为0
         */
        private Integer bottomOffset;

        /**
         * 左侧添加像素数，默认为0
         */
        private Integer leftOffset;

        /**
         * 右侧添加像素数，默认为0
         */
        private Integer rightOffset;

        /**
         * 是否添加水印，默认为false
         */
        private Boolean addWatermark;
    }
}
