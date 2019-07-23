package com.xc.util.jwt;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class JwtTokenUtil {
	/**
	 * 分钟
	 */
	private int expire;

	/**
	 * 加密码
	 */
	private String userSecret;

	private byte[] userPubKey;
	private byte[] userPriKey;

	private static JwtTokenUtil single;

	public JwtTokenUtil(){
		if(single==null){
			single = this;
		}else{
			throw new RuntimeException("已经创建,不能再创建JwtTokenUtil");
		}
	}

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

	public static JwtTokenUtil getSingle() {
		if(single==null){
			return new JwtTokenUtil();
		}
		return single;
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
