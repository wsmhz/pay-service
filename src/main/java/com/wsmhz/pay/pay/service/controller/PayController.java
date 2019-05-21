package com.wsmhz.pay.pay.service.controller;

import com.github.wxpay.sdk.WXPayUtil;
import com.wsmhz.pay.pay.service.api.api.PayServiceApi;
import com.wsmhz.pay.pay.service.api.domain.form.ali.AliPayPrecreateForm;
import com.wsmhz.pay.pay.service.api.domain.form.ali.AliRefundForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxPayUnifiedOrderForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxRefundForm;
import com.wsmhz.pay.pay.service.api.domain.vo.AliPayCheckSignResponseVo;
import com.wsmhz.pay.pay.service.api.domain.vo.AliPayResponseVo;
import com.wsmhz.pay.pay.service.api.domain.vo.WxPayResponseVo;
import com.wsmhz.pay.pay.service.service.AliPayService;
import com.wsmhz.pay.pay.service.service.WxPaySercice;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By TangBiJing On 2019/3/30
 * Description:
 */
@Slf4j
@RestController
public class PayController implements PayServiceApi {

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private WxPaySercice wxPaySercice;

    // ****************** 支付宝 *************************

    /**
     * 当面付 预下单支付(二维码)
     */
    @Override
    @PostMapping("/ali/pay/precreate")
    public AliPayResponseVo aliPayPrecreate(@RequestBody @Valid AliPayPrecreateForm aliPayPrecreateForm){
       return aliPayService.payPrecreate(aliPayPrecreateForm);
    }

    /**
     * 退款
     */
    @Override
    @PostMapping("/ali/refund")
    public boolean aliRefund(@RequestBody @Valid AliRefundForm aliRefundForm){
        return aliPayService.refund(aliRefundForm);
    }

    /**
     * 支付宝支付成功回调    支付回调验签地址在支付中心
     */
    @SneakyThrows
    @PostMapping("/ali/pay/notify")
    public String aliPayNotify(HttpServletRequest request){
        boolean result = aliPayService.notify(request);
        if(result){
            return "success";
        }
        return "fail";
    }

    /**
     * 支付宝支付成功回调验签   支付回调验签地址在业务项目
     */
    @Override
    @PostMapping("/ali/pay/checkSign")
    public AliPayCheckSignResponseVo aliPayCheckSign(@RequestBody Map<String,String> params){
        return aliPayService.checkSign(params);
    }

    // ***************** 微信 *************************

    /**
     * 微信统一下单
     */
    @Override
    @PostMapping("/wx/pay/precreate")
    public WxPayResponseVo wxPayUnifiedOrder(@RequestBody @Valid WxPayUnifiedOrderForm wxPayUnifiedOrderForm){
        return wxPaySercice.unifiedOrder(wxPayUnifiedOrderForm);
    }

    /**
     * 退款
     */
    @Override
    @PostMapping("/wx/refund")
    public boolean wxRefund(@RequestBody @Valid WxRefundForm wxRefundForm){
        return wxPaySercice.refund(wxRefundForm);
    }

    /**
     * 微信支付成功回调
     */
    @PostMapping("/wx/pay/notify")
    public void wxPayNotify(HttpServletRequest request , HttpServletResponse response){
        boolean signatureValid = wxPaySercice.checkSign(request);
        wxResponse(signatureValid, response);
    }

    /**
     * 微信退款成功回调
     */
    @PostMapping("/wx/refund/notify")
    public void wxRefundNotify(HttpServletRequest request , HttpServletResponse response){
        boolean result = wxPaySercice.checkRefundSign(request);
        wxResponse(result, response);
    }

    @SneakyThrows
    private void wxResponse(boolean result, HttpServletResponse response){
        if (result) {
            Map<String, String> responseMap = new HashMap<>(2);
            responseMap.put("return_code", "SUCCESS");
            responseMap.put("return_msg", "OK");
            String responseXml = WXPayUtil.mapToXml(responseMap);
            response.setContentType("text/xml");
            response.getWriter().write(responseXml);
            response.flushBuffer();
        }
    }
}
