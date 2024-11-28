package com.clothrent.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AliPayConfig {
    @Value("{alipay.appId}")
    public String appId;
    @Value("{alipay.merchantPrivateKey}")
    public String merchantPrivateKey;
    @Value("{alipay.alipayPublicKey}")
    public String alipayPublicKey;
    @Value("{alipay.notifyUrl}")
    public String notifyUrl;
}
