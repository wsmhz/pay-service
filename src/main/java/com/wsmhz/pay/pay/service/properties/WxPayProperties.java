package com.wsmhz.pay.pay.service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Created By TangBiJing On 2019/4/2
 * Description:
 */
@Getter
@Setter
public class WxPayProperties {

    // 微信支付分配的公众账号ID（企业号corpid即为此appId）
    private String appId;

    // 微信支付分配的商户号
    private String mchId;

    // API 密钥
    private String key;

    // API 沙箱环境密钥
    private String sandboxKey;

    // API证书绝对路径
    private String certPath;

    // 异步通知地址
    private String notifyUrl;

    // 是否沙箱环境
    private Boolean useSandbox;

    // HTTP(S) 连接超时时间，单位毫秒
    private int httpConnectTimeoutMs = 8000;

    // HTTP(S) 读数据超时时间，单位毫秒
    private int httpReadTimeoutMs = 10000;

}
