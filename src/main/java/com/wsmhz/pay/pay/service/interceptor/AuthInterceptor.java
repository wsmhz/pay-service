//package com.wsmhz.pay.pay.service.interceptor;
//
//import com.wsmhz.common.business.annotation.Exclude;
//import com.wsmhz.common.business.response.ResponseCode;
//import com.wsmhz.common.business.response.ServerResponse;
//import com.wsmhz.common.business.utils.JsonUtil;
//import com.wsmhz.common.business.utils.OAuthUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * Created By TangBiJing On 2019/5/15
// * Description:
// */
//@Slf4j
//@Component
//@ConditionalOnBean(OAuthUtil.class)
//@Exclude({"/ali/pay/notify","/ali/pay/checkSign", "/wx/pay/notify", "/wx/refund/notify", "/api/payInfo"})
//public class AuthInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    private OAuthUtil oAuthUtil;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//       if(oAuthUtil.checkToken(request)){
//            return true;
//        }
//        log.info("未登录拦截路径为：{}", request.getRequestURI());
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write(JsonUtil.objToString(ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请先登录")));
//        return false;
//    }
//}
