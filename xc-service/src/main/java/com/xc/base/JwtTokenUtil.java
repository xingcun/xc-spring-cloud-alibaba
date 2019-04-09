package com.xc.base;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xc.util.jwt.JWTHelper;
import com.xc.util.jwt.JWTInfo;
import com.xc.util.jwt.RsaKeyHelper;

/**
 * Created by ace on 2017/9/10.
 */
@Component
public class JwtTokenUtil {
	/**
	 * 分钟
	 */
    @Value("${jwt.expire}")
    private int expire;
    @Value("${jwt.rsa-secret}")
	private String userSecret;
    
	private byte[] userPubKey;
	private byte[] userPriKey;


    public String generateToken(JWTInfo jwtInfo) throws Exception {
        return JWTHelper.generateToken(jwtInfo, getUserPriKey(),expire);
    }

    public JWTInfo getInfoFromToken(String token) throws Exception {
        return JWTHelper.getInfoFromToken(token, getUserPubKey());
    }

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	public String getUserSecret() {
		return userSecret;
	}

	public void setUserSecret(String userSecret) {
		this.userSecret = userSecret;
		init();
	}

	public byte[] getUserPubKey() {
		if(userPubKey==null) {
			init();
		}
		return userPubKey;
	}

	public void setUserPubKey(byte[] userPubKey) {
		this.userPubKey = userPubKey;
	}

	public byte[] getUserPriKey() {
		if(userPriKey==null) {
			init();
		}
		return userPriKey;
	}

	public void setUserPriKey(byte[] userPriKey) {
		this.userPriKey = userPriKey;
	}


	public void init() {
		try {
			Map<String, byte[]> map = RsaKeyHelper.generateKey(userSecret);
			this.setUserPriKey(map.get("pri"));
			this.setUserPubKey(map.get("pub"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
