package com.wsmhz.pay.pay.service.enums;

/**
 * Created By TangBiJing On 2019/4/2
 * Description: 此为通信标识，非交易标识，交易是否成功需要查看result_code来判断
 */
public enum WxTradeStatus {

    SUCCESS,
    FAIL,
    UNKNOWN;

    private WxTradeStatus() {
    }
}
