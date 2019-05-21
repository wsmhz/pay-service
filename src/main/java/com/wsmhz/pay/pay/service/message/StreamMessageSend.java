package com.wsmhz.pay.pay.service.message;

import com.wsmhz.pay.pay.service.api.domain.form.ali.AliPaySuccessMessageForm;
import com.wsmhz.pay.pay.service.api.domain.form.ali.AliRefundMessageForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxPaySuccessMessageForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxRefundMessageForm;

/**
 * Created By TangBiJing On 2019/4/4
 * Description:
 */
public interface StreamMessageSend {

    // **** 支付宝 ****
    void aliPaySuccessOutput(AliPaySuccessMessageForm aliPaySuccessMessageForm);

    void aliRefundOutput(AliRefundMessageForm aliRefundMessageForm);

    // **** 微信 ****
    void wxPaySuccessOutput(WxPaySuccessMessageForm wxPaySuccessMessageForm);

    void wxRefundOutput(WxRefundMessageForm wxRefundMessageForm);
}
