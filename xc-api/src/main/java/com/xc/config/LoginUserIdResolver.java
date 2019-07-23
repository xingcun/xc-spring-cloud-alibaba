package com.xc.config;

import com.xc.annotation.LoginUserId;
import com.xc.util.jwt.JWTHelper;
import com.xc.util.jwt.JWTInfo;
import com.xc.util.jwt.JwtTokenUtil;
import com.xc.vo.CommonVariable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserIdResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //假如是在授权App中请求的数据,不获取loginUserId;
        if (methodParameter.hasParameterAnnotation(LoginUserId.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String token = nativeWebRequest.getHeader(CommonVariable.REQUEST_HEAD_TOKEN);
        JWTInfo info = null;
        if(token!=null) {

            try {
                info =  JWTHelper.getInfoFromToken(token, JwtTokenUtil.getSingle().getUserPubKey());
                return info.getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
