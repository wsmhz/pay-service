package com.wsmhz.pay.pay.service.service.impl;

import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.util.SignUtils;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.wsmhz.common.business.exception.BussinessException;
import com.wsmhz.common.business.utils.WebUtil;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxPaySuccessMessageForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxPayUnifiedOrderForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxRefundForm;
import com.wsmhz.pay.pay.service.api.domain.form.wx.WxRefundMessageForm;
import com.wsmhz.pay.pay.service.api.domain.vo.WxPayResponseVo;
import com.wsmhz.pay.pay.service.enums.WxTradeStatus;
import com.wsmhz.pay.pay.service.message.StreamMessageSend;
import com.wsmhz.pay.pay.service.properties.PayProperties;
import com.wsmhz.pay.pay.service.service.WxPaySercice;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By TangBiJing On 2019/4/2
 * Description: 微信支付
 */
@Slf4j
@Service
public class WxPaySerciceImpl implements WxPaySercice {

    @Autowired
    private WXPay wxpay;
    @Autowired
    private StreamMessageSend streamMessageSend;
    @Autowired
    private PayProperties payProperties;

    @Override
    public WxPayResponseVo unifiedOrder(WxPayUnifiedOrderForm wxPayUnifiedOrderForm) {
        log.info("微信支付参数为:{}", wxPayUnifiedOrderForm);
        Map<String, String> data = new HashMap<>();
        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().
                append("订单")
                .append(wxPayUnifiedOrderForm.getOrderNo())
                .append("购买商品共")
                .append(wxPayUnifiedOrderForm.getPayment())
                .append("元").toString();
        data.put("body", body);
        data.put("out_trade_no", wxPayUnifiedOrderForm.getOrderNo());
        data.put("device_info", wxPayUnifiedOrderForm.getDeviceInfo());
        data.put("fee_type", wxPayUnifiedOrderForm.getFeeType());
        data.put("total_fee", wxPayUnifiedOrderForm.getPayment());
        // 支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
        data.put("spbill_create_ip", WebUtil.getClientIP());
        data.put("notify_url", wxPayUnifiedOrderForm.getNotifyUrl());
        data.put("trade_type", wxPayUnifiedOrderForm.getTradeType());
        data.put("product_id", wxPayUnifiedOrderForm.getProductId());
        data.put("openid", wxPayUnifiedOrderForm.getOpenId());
        // 统一下单
        Map<String, String> result = null;
        try {
            result = wxpay.unifiedOrder(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BussinessException(e.getMessage());
        }
        log.info("微信下单返回结果为：{}", result);
        String nonceStr = String.valueOf(System.currentTimeMillis());
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String prepay_id = result.get("prepay_id");
        Map<String, String> payInfo = new HashMap<>();
        payInfo.put("appId", payProperties.getWx().getAppId());
        payInfo.put("timeStamp", timeStamp);
        payInfo.put("nonceStr", nonceStr);
        payInfo.put("package", "prepay_id=" + prepay_id);
        payInfo.put("signType", WxPayConstants.SignType.MD5);
        String paySign = SignUtils.createSign(payInfo, payProperties.getWx().getKey(), null);

        return checkResponse(result, WxPayResponseVo.builder()
                                        .prepayId(prepay_id)
                                        .qrCode(result.get("code_url"))
                                        .tradeType(result.get("trade_type"))
                                        .nonceStr(nonceStr)
                                        .signType(WxPayConstants.SignType.MD5)
                                        .packageValue("prepay_id=" + prepay_id)
                                        .appId(payProperties.getWx().getAppId())
                                        .paySign(paySign)
                                        .timeStamp(timeStamp).build());
    }

    @SneakyThrows
    @Override
    public boolean checkSign(HttpServletRequest request) {
        String dataXml = getNotifyParameter(request);
        Map<String, String> notifyMap = WXPayUtil.xmlToMap(dataXml);
        log.info("微信验签参数为：{}", notifyMap);
        boolean checkResult = wxpay.isPayResultNotifySignatureValid(notifyMap);
        if(checkResult){
            log.info("订单号：{}验签成功", notifyMap.get("out_trade_no"));
            // 业务调用方还需要验证数据, 目前采用消息模式有以下问题，1.数据不及时反馈是否正确都返回给微信成功状态  2.消息服务宕机，或未被消费情况
            // 实际应用应改为直接接口调用模式
            // 校验返回的订单对应业务数据的状态，防止数据泄漏导致出现“假通知”，造成资金损失
            // 要采用数据锁进行并发控制，以避免函数重入造成的数据混乱
            // TODO 处理业务
            streamMessageSend.wxPaySuccessOutput(
                    WxPaySuccessMessageForm.builder()
                    .deviceInfo(notifyMap.get("device_info"))
                    .openId(notifyMap.get("openid"))
                    .subscribe(notifyMap.get("is_subscribe"))
                    .tradeType(notifyMap.get("trade_type"))
                    .bankType(notifyMap.get("bank_type"))
                    .totalFee(notifyMap.get("total_fee"))
                    .settlementTotalFee("settlement_total_fee")
                    .feeType("fee_type")
                    .transactionId(notifyMap.get("transaction_id"))
                    .outTradeNo(notifyMap.get("out_trade_no"))
                    .attach(notifyMap.get("attach"))
                    .timeEnd(notifyMap.get("time_end"))
                    .build()
            );
            return true;
        }
        log.error("订单号：{}验签失败", notifyMap.get("out_trade_no"));
        return false;
    }

    /**
     * 调用申请退款、撤销订单接口需要商户证书
     */
    @SneakyThrows
    @Override
    public boolean refund(WxRefundForm wxRefundForm) {
        log.info("微信退款参数为：{}", wxRefundForm);
        Map<String, String> reqData = new HashMap<>();
        reqData.put("out_trade_no", wxRefundForm.getOutTradeNo());
        reqData.put("out_refund_no", wxRefundForm.getOutRefundNo());
        // 订单总金额，单位为分，只能为整数
        reqData.put("total_fee", wxRefundForm.getTotalFee());
        reqData.put("refund_fee", wxRefundForm.getRefundFee());
        reqData.put("notify_url", wxRefundForm.getNotifyUrl());
        reqData.put("refund_fee_type", wxRefundForm.getRefundFeeType());

        Map<String, String> result = wxpay.refund(reqData);
        log.info("微信退款结果为：{}", result);
        return checkResponse(result, true);
    }

    /**
     * 解密退款通知
     * https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_16&index=11
     */
    @SneakyThrows
    @Override
    public boolean checkRefundSign(HttpServletRequest request) {
        String dataXml = getNotifyParameter(request);
        Map<String, String> notifyMap = WXPayUtil.xmlToMap(dataXml);
        log.info("微信退款验签参数为：{}", notifyMap);
        Boolean checkResult = checkResponse(notifyMap, true);
        if(checkResult){
            String reqInfo = notifyMap.get("req_info");
            //（1）对加密串A做base64解码，得到加密串B
            byte[] bytes = new BASE64Decoder().decodeBuffer(reqInfo);
            //（2）对商户key做md5，得到32位小写key* ( key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置 )
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(WXPayUtil.MD5(payProperties.getWx().getKey()).toLowerCase().getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            //（3）用key*对加密串B做AES-256-ECB解密（PKCS7Padding）https://www.cnblogs.com/yaks/p/5608358.html
            String responseXml = new String(cipher.doFinal(bytes),StandardCharsets.UTF_8);
            Map<String, String> data = WXPayUtil.xmlToMap(responseXml);
            // 业务调用方还需要验证数据, 目前采用消息模式有以下问题，1.数据不及时反馈是否正确都返回给微信成功状态  2.消息服务宕机，或未被消费情况
            // 实际应用应改为直接接口调用模式
            // 校验返回的订单对应业务数据的状态，防止数据泄漏导致出现“假通知”，造成资金损失
            // 要采用数据锁进行并发控制，以避免函数重入造成的数据混乱
            // TODO 处理业务
            streamMessageSend.wxRefundOutput(
                    WxRefundMessageForm.builder()
                        .transactionId(data.get("transaction_id"))
                        .outTradeNo(data.get("out_trade_no"))
                        .refundId(data.get("refund_id"))
                        .outRefundNo(data.get("out_refund_no"))
                        .totalFee(data.get("total_fee"))
                        .refundFee(data.get("refund_fee"))
                        .refundStatus(data.get("refund_status"))
                        .successTime(data.get("success_time"))
                        .refundRecvAccout(data.get("refund_recv_accout"))
                        .refundAccount(data.get("refund_account"))
                        .refundQequestSource(data.get("refund_request_source")).build());
        }
        return checkResult;
    }


    @SneakyThrows
    private String getNotifyParameter(HttpServletRequest request){
        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, length);
        }
        outSteam.close();
        inputStream.close();
        // 获取微信调用我们notify_url的返回信息
        return new String(outSteam.toByteArray(), StandardCharsets.UTF_8);
    }

    private <T> T checkResponse(Map<String, String> checkData, T returnData){
        if(checkData.get("return_code").equals(WxTradeStatus.SUCCESS.name())){ // 通讯是否成功
            if(checkData.get("result_code").equals(WxTradeStatus.SUCCESS.name())){ // 业务是否成功
                return returnData;
            }
            return (T) (checkData.get("err_code") + "：" + checkData.get("err_code_des"));
        }
        return (T) checkData.get("return_msg");
    }
}
