package com.xc.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.xc.util.jwt.JWTHelper;
import com.xc.util.jwt.JWTInfo;
import com.xc.vo.CommonVariable;

public class LoginUserHolder   {
	
	private static byte[] userPubKey;

	
	public static JWTInfo getLoginUser() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		String token = request.getHeader(CommonVariable.REQUEST_HEAD_TOKEN);
		JWTInfo info = null;
		if(token!=null) {
			
			try {
				 info =  JWTHelper.getInfoFromToken(token,userPubKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return info;
	}

	public static void setUserPubKey(byte[] userPubKey) {
		LoginUserHolder.userPubKey = userPubKey;
	}

	
}
