package com.wsmhz.pay.pay.service.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Created By TangBiJing On 2019/4/3
 * Description:
 */
@Getter
@Setter
public class AliPayProperties {

    // 支付宝网关名
    private String openApiDomain;

    private String mcloudApiDomain;

    private String pId;

    private String appId;

    // RSA私钥
    private String privateKey;

    // RSA公钥
    private String publicKey;

    // SHA256withRsa对应支付宝公钥
    private String alipayPublicKey;

    private String signType;

    // 当面付最大查询次数
    private Integer maxQueryRetry;

    // 当面付查询间隔（毫秒）
    private Integer queryDuration;

    // 当面付最大撤销次数
    private Integer maxCancelRetry;

    // 当面付撤销间隔（毫秒）
    private Integer cancelDuration;

    // 交易保障线程第一次调度延迟
    private Integer heartbeatDelay;

    // 交易保障线程调度间隔（秒）
    private Integer heartbeatDuration;
}
