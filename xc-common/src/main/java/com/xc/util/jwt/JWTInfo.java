package com.xc.util.jwt;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by ace on 2017/9/10.
 */
public class JWTInfo extends HashMap implements Serializable, IJWTInfo {
	private String username;
	private String id;
	private String nickName;

	public JWTInfo() {

	}

	public JWTInfo(String username, String userId, String name) {
		setUsername(username);
		setId(userId);
		setNickName(name);
	}

	@Override
	public String getUsername() {
		return (String) this.get("username");
	}

	public void setUsername(String username) {
		this.put("username", username);
		this.username = username;
	}

	@Override
	public String getId() {
		return (String) this.get("id");
	}

	public void setId(String userId) {
		this.put("id", userId);
		this.id = userId;
	}

	public String getNickName() {
		return (String) this.get("nickName");
	}

	public void setNickName(String nickName) {
		this.put("nickName", nickName);
		this.nickName = nickName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		JWTInfo jwtInfo = (JWTInfo) o;

		if (username != null ? !username.equals(jwtInfo.username) : jwtInfo.username != null) {
			return false;
		}
		return id != null ? id.equals(jwtInfo.id) : jwtInfo.id == null;

	}

}
