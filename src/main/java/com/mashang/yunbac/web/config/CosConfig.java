package com.mashang.yunbac.web.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class CosConfig {

    /**
     * 域名
     */
    @Value("${cos.client.host}")
    private String host;

    /**
     * secretId
     */
    @Value("${cos.client.secretId}")
    private String secretId;

    /**
     * 密钥
     */
    @Value("${cos.client.secretKey}")
    private String secretKey;

    /**
     * 区域
     */
    @Value("${cos.client.region}")
    private String region;

    /**
     * 桶名
     */
    @Value("${cos.client.bucket}")
    private String bucket;

    @Bean
    public COSClient getCosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }

}
