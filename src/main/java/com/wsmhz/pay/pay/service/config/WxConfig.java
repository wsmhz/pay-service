package com.wsmhz.pay.pay.service.config;

import com.github.wxpay.sdk.WXPayConfig;
import com.wsmhz.pay.pay.service.properties.PayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created By TangBiJing On 2019/4/2
 * Description:
 */
@Component
@Slf4j
public class WxConfig  implements WXPayConfig {

    @Autowired
    private PayProperties payProperties;

    @Override
    public String getAppID() {
        return payProperties.getWx().getAppId();
    }

    @Override
    public String getMchID() {
        return payProperties.getWx().getMchId();
    }

    @Override
    public String getKey(){
        if (payProperties.getWx().getUseSandbox()) {
            return payProperties.getWx().getSandboxKey();
        }
        return payProperties.getWx().getKey();
    }

    /**
     * 获取商户证书内容
     * 商户证书 [pkcs12格式(apiclient_cert.p12);微信商户平台(pay.weixin.qq.com)–>账户中心–>账户设置–>API安全–>证书下载 ]
     */
    @Override
    public InputStream getCertStream()  {
        File certFile = new File(payProperties.getWx().getCertPath());
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(certFile);
        } catch (FileNotFoundException e) {
            log.error("cert file not found, path={}, exception is:{}", payProperties.getWx().getCertPath(), e);
        }
        return inputStream;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return payProperties.getWx().getHttpConnectTimeoutMs();
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return payProperties.getWx().getHttpReadTimeoutMs();
    }
}
