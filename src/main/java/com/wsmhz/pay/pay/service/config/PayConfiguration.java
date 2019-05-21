package com.wsmhz.pay.pay.service.config;

import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.wsmhz.pay.pay.service.properties.PayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created By TangBiJing On 2019/4/3
 * Description:
 */
@Configuration
public class PayConfiguration {

    @Autowired
    private WxConfig wxConfig;

    @Autowired
    private PayProperties payProperties;

    @Bean
    public WXPay wxPay() {
        return new WXPay(wxConfig, WXPayConstants.SignType.MD5, payProperties.getWx().getUseSandbox() );
    }

    @Bean
    public AlipayTradeService alipayTradeService() {
        // 可使用com.alipay.demo.trade.config下的Configs初始化其他参数
        return new AlipayTradeServiceImpl.ClientBuilder()
                .setGatewayUrl(payProperties.getAli().getOpenApiDomain())
                .setAppid(payProperties.getAli().getAppId())
                .setPrivateKey(payProperties.getAli().getPrivateKey())
                .setAlipayPublicKey(payProperties.getAli().getAlipayPublicKey())
                .setSignType(payProperties.getAli().getSignType())
                .build();
    }
}
