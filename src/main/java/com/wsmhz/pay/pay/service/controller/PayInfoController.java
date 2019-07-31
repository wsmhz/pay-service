package com.wsmhz.pay.pay.service.controller;

import com.wsmhz.common.business.annotation.UnAuth;
import com.wsmhz.common.business.utils.DozerBeanUtil;
import com.wsmhz.pay.pay.service.api.api.PayInfoApi;
import com.wsmhz.pay.pay.service.api.domain.form.PayInfoInSertForm;
import com.wsmhz.pay.pay.service.domain.entity.PayInfo;
import com.wsmhz.pay.pay.service.service.PayInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created By TangBiJing On 2019/3/30
 * Description:
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class PayInfoController implements PayInfoApi {

    @Autowired
    private PayInfoService payInfoService;

    @UnAuth
    @Override
    @PostMapping("/payInfo")
    public int insertSelective(@RequestBody @Valid PayInfoInSertForm payInfoInSertForm){
        return payInfoService.insertSelective(DozerBeanUtil.map(payInfoInSertForm, PayInfo.class));
    }
}
