package com.wsmhz.pay.pay.service.service;

import com.wsmhz.pay.pay.service.api.domain.form.ali.AliPayPrecreateForm;
import com.wsmhz.pay.pay.service.api.domain.form.ali.AliRefundForm;
import com.wsmhz.pay.pay.service.api.domain.vo.AliPayCheckSignResponseVo;
import com.wsmhz.pay.pay.service.api.domain.vo.AliPayResponseVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created By TangBiJing On 2019/3/30
 * Description:
 */
public interface AliPayService {

    /**
     * 当面付 预下单(二维码)
     */
    AliPayResponseVo payPrecreate(AliPayPrecreateForm aliPayPrecreateForm);

    /**
     * 退款
     */
    boolean refund(AliRefundForm aliRefundForm);

    /**
     * 验证回调的正确性,是不是支付宝发的.业务调用方还需要验证避免重复通知.
     */
    boolean notify(HttpServletRequest request);

    /**
     * 验证回调的正确性,是不是支付宝发的.业务调用方还需要验证避免重复通知.
     */
    AliPayCheckSignResponseVo checkSign(Map<String,String> params);
}
