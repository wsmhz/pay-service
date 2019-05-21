package com.wsmhz.pay.pay.service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created By TangBiJing On 2019/4/3
 * Description:
 */
@Configuration
@ConfigurationProperties(prefix = "wsmhz.pay")
@EnableConfigurationProperties(PayProperties.class)
@Getter
@Setter
public class PayProperties {

    private WxPayProperties wx = new WxPayProperties();

    private AliPayProperties ali = new AliPayProperties();
}
