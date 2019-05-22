package com.wsmhz.pay.pay.service.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.google.common.collect.Maps;
import com.wsmhz.pay.pay.service.api.domain.form.ali.AliPayPrecreateForm;
import com.wsmhz.pay.pay.service.api.domain.form.ali.AliPaySuccessMessageForm;
import com.wsmhz.pay.pay.service.api.domain.form.ali.AliRefundForm;
import com.wsmhz.pay.pay.service.api.domain.form.ali.AliRefundMessageForm;
import com.wsmhz.pay.pay.service.api.domain.vo.AliPayCheckSignResponseVo;
import com.wsmhz.pay.pay.service.api.domain.vo.AliPayResponseVo;
import com.wsmhz.pay.pay.service.enums.AliPayResponseEnum;
import com.wsmhz.pay.pay.service.enums.AliRefundResponseEnum;
import com.wsmhz.pay.pay.service.message.StreamMessageSend;
import com.wsmhz.pay.pay.service.properties.PayProperties;
import com.wsmhz.pay.pay.service.service.AliPayService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created By TangBiJing On 2019/3/30
 * Description:
 */
@Slf4j
@Service
public class AliPayServiceImpl implements AliPayService {

    @Autowired
    private AlipayTradeService tradeService;
    @Autowired
    private StreamMessageSend streamMessageSend;
    @Autowired
    private PayProperties payProperties;

    public AliPayResponseVo payPrecreate(AliPayPrecreateForm aliPayPrecreateForm) {
        log.info("支付宝支付参数为:{}", aliPayPrecreateForm);
        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder()
                .append(aliPayPrecreateForm.getBusinessSystemName())
                .append("扫码支付,订单号:")
                .append(aliPayPrecreateForm.getOrderNo()).toString();
        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().
                append("订单")
                .append(aliPayPrecreateForm.getOrderNo())
                .append("购买商品共")
                .append(aliPayPrecreateForm.getPayment())
                .append("元").toString();
        // 商品明细列表，需填写购买商品详细信息
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        aliPayPrecreateForm.getOrderItemList()
                .forEach(item -> {
                    goodsDetailList.add(
                        GoodsDetail.newInstance(item.getProductId(),
                            item.getProductName(),
                            Long.parseLong(item.getPrice()),
                            item.getQuantity())
                    );
                });
        // 创建扫码支付请求builder，设置请求参数
        ExtendParams extendParams = new ExtendParams();
        if(aliPayPrecreateForm.getExtendParamsForm() != null){
            extendParams.setSysServiceProviderId(aliPayPrecreateForm.getExtendParamsForm().getSysServiceProviderId());
        }
        AlipayTradePrecreateRequestBuilder alipayTradeBuilder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject)
                .setTotalAmount(aliPayPrecreateForm.getPayment())
                .setOutTradeNo(aliPayPrecreateForm.getOrderNo())
                .setUndiscountableAmount(aliPayPrecreateForm.getUndiscountableAmount())
                .setSellerId(aliPayPrecreateForm.getSellerId())
                .setBody(body)
                .setOperatorId(aliPayPrecreateForm.getOperatorId())
                .setStoreId(aliPayPrecreateForm.getStoreId())
                .setExtendParams(extendParams)
                .setTimeoutExpress(aliPayPrecreateForm.getTimeoutExpress())
                .setNotifyUrl(aliPayPrecreateForm.getNotifyUrl())
                .setGoodsDetailList(goodsDetailList);
        // 开始预下单
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(alipayTradeBuilder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info(AliPayResponseEnum.SUCCESS.getValue());
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                return AliPayResponseVo.builder()
                        .orderNo(response.getOutTradeNo())
                        .qrCode(response.getQrCode()).build();
            case FAILED:
                log.error(AliPayResponseEnum.FAILED.getValue());
                return null;
            case UNKNOWN:
                log.error(AliPayResponseEnum.UNKNOWN.getValue());
                return null;
            default:
                log.error(AliPayResponseEnum.DEFAULT.getValue());
                return null;
        }
    }

    @Override
    public boolean refund(AliRefundForm aliRefundForm) {
        // 创建退款请求builder，设置请求参数
        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
                .setOutTradeNo(aliRefundForm.getOrderNo())
                .setRefundAmount(aliRefundForm.getAmount())
                .setRefundReason(aliRefundForm.getReason())
                .setOutRequestNo(aliRefundForm.getOutRequestNo())
                .setStoreId(aliRefundForm.getStoreId());
        AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
        AlipayTradeRefundResponse response = result.getResponse();
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info(AliRefundResponseEnum.SUCCESS.getValue());
                // 通知业务方 TODO
                streamMessageSend.aliRefundOutput(
                        AliRefundMessageForm.builder()
                            .tradeNo(response.getTradeNo())
                            .outTradeNo(response.getOutTradeNo())
                            .buyerLogonId(response.getBuyerLogonId())
                            .fundChange(response.getFundChange())
                            .refundFee(response.getRefundFee())
                            .gmtrRefundPay(response.getGmtRefundPay()).build());
                return true;
            case FAILED:
                log.error(AliRefundResponseEnum.FAILED.getValue());
                return false;
            case UNKNOWN:
                log.error(AliRefundResponseEnum.UNKNOWN.getValue());
                return false;
            default:
                log.error(AliRefundResponseEnum.DEFAULT.getValue());
                return false;
        }
    }

    @SneakyThrows
    @Override
    public boolean notify(HttpServletRequest request) {
        Map<String,String> params = Maps.newHashMap();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iterator = requestParams.keySet().iterator(); iterator.hasNext(); ) {
            String name = iterator.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        log.info("支付宝回调参数:{}",params.toString());
        params.remove("sign_type");
        boolean checkResult = checkSign(params).isChecked();
        if(checkResult){
            log.info("订单号：{}验签成功", params.get("out_trade_no"));
            // 业务调用方还需要验证数据, 目前采用消息模式有以下问题，1.数据不及时反馈是否正确都返回给成功状态  2.消息服务宕机，或未被消费情况
            // 实际应用应改为直接接口调用模式
            // 校验返回的订单对应业务数据的状态，防止数据泄漏导致出现“假通知”，造成资金损失
            // 要采用数据锁进行并发控制，以避免函数重入造成的数据混乱
            // TODO 处理业务
            streamMessageSend.aliPaySuccessOutput(
                    AliPaySuccessMessageForm.builder()
                        .tradeNo(params.get("trade_no"))
                        .outTradeNo(params.get("out_trade_no"))
                        .buyerId(params.get("buyer_id"))
                        .buyerLogonId(params.get("buyer_logon_id"))
                        .tradeStatus(params.get("trade_status"))
                        .totalAmount(params.get("total_amount"))
                        .buyerPayAmount(params.get("buyer_pay_amount"))
                        .refundFee(params.get("refund_fee"))
                        .sendBackFee(params.get("send_back_fee"))
                        .subject(params.get("subject"))
                        .body(params.get("body"))
                        .gmtPayment(params.get("gmt_payment")).build());
            return true;
        }
        log.error("订单号：{}非法请求,验签不通过", params.get("out_trade_no"));
        return false;
    }

    @SneakyThrows
    @Override
    public AliPayCheckSignResponseVo checkSign(Map<String,String> params) {
        boolean checked = AlipaySignature.rsaCheckV2(params, payProperties.getAli().getAlipayPublicKey(),"utf-8",payProperties.getAli().getSignType());
        return AliPayCheckSignResponseVo.builder()
                .checked(checked)
                .msg("Alipay验签结果为：" + checked).build();
    }

    // 输出响应信息
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
}
