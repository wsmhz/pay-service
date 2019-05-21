package com.wsmhz.pay.pay.service.enums;

/**
 * Created By TangBiJing On 2019/3/30
 * Description: 支付宝退款响应信息
 */
public enum AliRefundResponseEnum {

    SUCCESS(1, "支付宝退款成功"),
    FAILED(2 , "支付宝退款失败"),
    UNKNOWN(3, "支付宝系统异常，订单退款状态未知"),
    DEFAULT(4, "不支持的交易状态，交易返回异常");


    private String value;
    private int code;

    AliRefundResponseEnum(int code, String value){
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }
}
