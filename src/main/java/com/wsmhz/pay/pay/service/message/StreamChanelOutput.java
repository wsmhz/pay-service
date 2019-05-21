package com.wsmhz.pay.pay.service.message;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Created By TangBiJing On 2019/4/3
 * Description: 消息发送通道
 */
public interface StreamChanelOutput {

    // 微信支付成功异步通知通道
    String WX_PAY_SUCCESS_OUTPUT = "wx_pay_success_output";

    // 微信退款异步通知通道
    String WX_REFUND_OUTPUT = "wx_refund_output";

    // 支付宝支付成功异步通知通道
    String ALI_PAY_SUCCESS_OUTPUT = "ali_pay_success_output";

    // 支付宝退款异步通知通道
    String ALI_REFUND_OUTPUT = "ali_refund_output";

    @Output(WX_PAY_SUCCESS_OUTPUT)
    MessageChannel wxPaySuccessOutput();

    @Output(WX_REFUND_OUTPUT)
    MessageChannel wxRefundOutput();

    @Output(ALI_PAY_SUCCESS_OUTPUT)
    MessageChannel aliPaySuccessOutput();

    @Output(ALI_REFUND_OUTPUT)
    MessageChannel aliRefundOutput();
}
