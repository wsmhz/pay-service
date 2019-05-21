package com.wsmhz.pay.pay.service.message;

import com.wsmhz.pay.pay.service.api.domain.form.ali.AliPaySuccessMessageForm;
import com.wsmhz.pay.pay.service.api.domain.form.ali.AliRefundMessageForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxPaySuccessMessageForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxRefundMessageForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * Created By TangBiJing On 2019/4/4
 * Description:
 */
@EnableBinding(StreamChanelOutput.class)
@Slf4j
public class StreamMessageSendImpl implements StreamMessageSend {

    @Autowired
    private StreamChanelOutput streamChanelOutput;

    @Override
    public void aliPaySuccessOutput(AliPaySuccessMessageForm aliPaySuccessMessageForm) {
        Message<AliPaySuccessMessageForm> message = MessageBuilder.withPayload(aliPaySuccessMessageForm).build();
        streamChanelOutput.aliPaySuccessOutput().send(message);
    }

    /**
     * 支付宝退款
     */
    @Override
    public void aliRefundOutput(AliRefundMessageForm aliRefundMessageForm) {
        Message<AliRefundMessageForm> message = MessageBuilder.withPayload(aliRefundMessageForm).build();
        streamChanelOutput.aliRefundOutput().send(message);
    }

    /**
     * 微信支付成功异步通知
     */
    @Override
    public void wxPaySuccessOutput(WxPaySuccessMessageForm wxPaySuccessMessageForm) {
        Message<WxPaySuccessMessageForm> message = MessageBuilder.withPayload(wxPaySuccessMessageForm).build();
        streamChanelOutput.wxPaySuccessOutput().send(message);
    }

    /**
     * 微信退款异步通知
     */
    @Override
    public void wxRefundOutput(WxRefundMessageForm wxRefundMessageForm) {
        Message<WxRefundMessageForm> message = MessageBuilder.withPayload(wxRefundMessageForm).build();
        streamChanelOutput.wxRefundOutput().send(message);
    }


}
