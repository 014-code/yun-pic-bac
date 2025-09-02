package com.mashang.yunbac.web.api.imagesearch;

import com.mashang.yunbac.web.entity.enums.ErrorCode;
import com.mashang.yunbac.web.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GetSoImageUrlApi {
    public static String getSoImageUrl(String imageUrl) {
        String url = "https://st.so.com/r?src=st&srcsp=home&img_url=" + imageUrl + "&submittype=imgurl";
        try {
            Document document = Jsoup.connect(url).timeout(10000).get();
            Element imgElement = document.selectFirst(".img_img");
            if (imgElement != null) {
                String soImageUrl = "";
                // 获取当前元素的属性
                String style = imgElement.attr("style");
                if (style.contains("background-image:url(")) {
                    // 提取URL部分
                    int start = style.indexOf("url(") + 4;  // 从"Url("之后开始
                    int end = style.indexOf(")", start);    // 找到右括号的位置
                    if (start > 4 && end > start) {
                        soImageUrl = style.substring(start, end);
                    }
                }
                return soImageUrl;
            }
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
        } catch (Exception e) {
            log.error("搜图失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
        }
    }
}
