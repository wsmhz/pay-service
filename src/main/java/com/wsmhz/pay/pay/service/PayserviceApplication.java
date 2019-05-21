package com.wsmhz.pay.pay.service;

import org.springframework.boot.SpringApplication;

import com.wsmhz.common.business.annotation.WsmhzMicroServiceApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created By TangBiJing
 * Description: 
 */
@EnableFeignClients("com.wsmhz")
@ComponentScan("com.wsmhz.**")
@SpringCloudApplication
@WsmhzMicroServiceApplication
public class PayserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayserviceApplication.class, args);
    }
}