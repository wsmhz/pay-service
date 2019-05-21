package com.wsmhz.pay.pay.service.controller;

import com.google.common.collect.Lists;
import com.wsmhz.common.business.exception.BussinessException;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxOrderItemForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxPayUnifiedOrderForm;
import com.wsmhz.pay.pay.service.api.domain.vo.WxPayResponseVo;
import com.wsmhz.pay.pay.service.service.AliPayService;
import com.wsmhz.pay.pay.service.service.WxPaySercice;
import com.wsmhz.common.business.utils.ZxingUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created By TangBiJing On 2019/4/15
 * Description:
 */
@Controller
@Slf4j
public class PayTestController {

    private static final String ALIPAY = "Alipay";
    private static final String WXPAY = "MicroMessenger";

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private WxPaySercice wxPaySercice;

    // *********** 聚合支付测试 *************
    @SneakyThrows
    @GetMapping(value = "/pay",produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] pay(HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        File file = ZxingUtils.getQRCodeImge("http://172.30.34.54:8101/paytest", 256, path);
        return IOUtils.toByteArray(new FileInputStream(file));
    }

    @GetMapping(value = "/paytest")
    public String paytest(Model model, HttpServletRequest request, HttpServletResponse response){
        log.info("====== 开始接收二维码扫码支付请求 ======");
        String ua = request.getHeader("User-Agent");
        log.info("【二维码扫码支付】接收参数: ua={}", ua);
        if(ua.contains(ALIPAY)){
            log.info("发起支付宝请求");
        } else if(ua.contains(WXPAY)){
            log.info("发起微信请求");
            String openId = request.getParameter("openId");
            if (StringUtils.isBlank(openId)) {
                String GetOpenIdURL2 = "http://tbj.n.yumc.pw/goods/getOpenId";
                String QR_PAY_URL_my = "http://172.30.34.54:8101/paytest";
                String redirectUrl = QR_PAY_URL_my + "?amount=" + 2;
                String url = GetOpenIdURL2 + "?redirectUrl=" + redirectUrl;
                log.info("跳转URL={}", url);
                return "redirect:" + url;
            }
            WxPayResponseVo wxPayResponseDto = wxPaySercice.unifiedOrder(WxPayUnifiedOrderForm.builder()
                                        .businessSystemName("web-test")
                                        .openId(openId)
                                        .platform("wx")
                                        .orderNo(String.valueOf(System.nanoTime()))
                                        .payment("2")
                                        .notifyUrl("https://tbj.n.yumc.pw/api/wx/pay/notify")
                                        .tradeType("JSAPI")
                                        .productId("1")
                                        .deviceInfo("WEB")
                                        .feeType("CNY")
                                        .orderItemList(Lists.newArrayList(
                                                WxOrderItemForm.builder()
                                                        .productId("1")
                                                        .productName("测试")
                                                        .quantity(1)
                                                        .price("2").build()
                                        )).build());
            model.addAttribute("payResult", wxPayResponseDto);
            log.info("微信支付结果响应: {}", wxPayResponseDto);
        } else {
            throw new BussinessException("不支持的扫码设备类型");
        }
        return "pay";
    }

}
