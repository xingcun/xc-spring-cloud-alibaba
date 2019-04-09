package com.xc.util.jwt;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JWTInfo implements Serializable {
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


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
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

	public JSONObject toJsonObj() {
		return JSON.parseObject(JSON.toJSONString(this));
	}
	
	public static JWTInfo of(Object body) {
		return JSON.parseObject(JSON.toJSONString(body),JWTInfo.class);
	}
}
