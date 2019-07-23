package com.xc.util;

import javax.servlet.http.HttpServletRequest;

import com.xc.util.jwt.JwtTokenUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.xc.util.jwt.JWTHelper;
import com.xc.util.jwt.JWTInfo;
import com.xc.vo.CommonVariable;

public class LoginUserHolder   {

	public static JWTInfo getLoginUser() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		String token = request.getHeader(CommonVariable.REQUEST_HEAD_TOKEN);
		JWTInfo info = null;
		if(token!=null) {

			try {
				 info =  JWTHelper.getInfoFromToken(token, JwtTokenUtil.getSingle().getUserPubKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return info;
	}



}
