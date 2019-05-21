package com.wsmhz.pay.pay.service.service;

import com.wsmhz.common.business.response.ServerResponse;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxPayUnifiedOrderForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxRefundForm;
import com.wsmhz.pay.pay.service.api.domain.vo.WxPayResponseVo;

import javax.servlet.http.HttpServletRequest;

/**
 * Created By TangBiJing On 2019/4/2
 * Description: 微信支付
 */
public interface WxPaySercice {

    WxPayResponseVo unifiedOrder(WxPayUnifiedOrderForm wxPayUnifiedOrderForm);

    boolean checkSign(HttpServletRequest request);

    boolean refund(WxRefundForm wxRefundForm);

    boolean checkRefundSign(HttpServletRequest request);
}
