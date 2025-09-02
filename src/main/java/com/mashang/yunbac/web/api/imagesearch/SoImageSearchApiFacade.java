package com.mashang.yunbac.web.api.imagesearch;

import com.mashang.yunbac.web.api.imagesearch.vo.ImageSearchVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 以图搜图门面方法
 */
@Component
@Slf4j
public class SoImageSearchApiFacade {

    /**
     * 搜索图片
     *
     * @param imageUrl 需要以图搜图的图片地址
     * @param start    开始下表
     * @return 图片搜索结果列表
     */
    public static List<ImageSearchVo> searchImage(String imageUrl, Integer start) {
        String soImageUrl = GetSoImageUrlApi.getSoImageUrl(imageUrl);
        List<ImageSearchVo> imageList = GetSoImageListApi.getImageList(soImageUrl, start);
        return imageList;
    }
}
