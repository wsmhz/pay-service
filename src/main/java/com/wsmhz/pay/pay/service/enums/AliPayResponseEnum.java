package com.wsmhz.pay.pay.service.enums;

/**
 * Created By TangBiJing On 2019/3/30
 * Description: 支付宝预下单响应信息
 */
public enum AliPayResponseEnum {

    SUCCESS(1, "支付宝预下单成功"),
    FAILED(2 , "支付宝预下单失败"),
    UNKNOWN(3, "支付宝系统异常，预下单状态未知"),
    DEFAULT(4, "不支持的交易状态，交易返回异常");


    private String value;
    private int code;

    AliPayResponseEnum(int code,String value){
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
