package com.xc.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author ace
 * @create 2017/12/17.
 */
@Configuration
public class KeyConfiguration {
	@Value("${jwt.rsa-secret}")
	private String userSecret;
	@Value("${client.rsa-secret}")
	private String serviceSecret;
	private byte[] userPubKey;
	private byte[] userPriKey;
	private byte[] servicePriKey;
	private byte[] servicePubKey;

	public String getUserSecret() {
		return userSecret;
	}

	public void setUserSecret(String userSecret) {
		this.userSecret = userSecret;
	}

	public String getServiceSecret() {
		return serviceSecret;
	}

	public void setServiceSecret(String serviceSecret) {
		this.serviceSecret = serviceSecret;
	}

	public byte[] getUserPubKey() {
		return userPubKey;
	}

	public void setUserPubKey(byte[] userPubKey) {
		this.userPubKey = userPubKey;
	}

	public byte[] getUserPriKey() {
		return userPriKey;
	}

	public void setUserPriKey(byte[] userPriKey) {
		this.userPriKey = userPriKey;
	}

	public byte[] getServicePriKey() {
		return servicePriKey;
	}

	public void setServicePriKey(byte[] servicePriKey) {
		this.servicePriKey = servicePriKey;
	}

	public byte[] getServicePubKey() {
		return servicePubKey;
	}

	public void setServicePubKey(byte[] servicePubKey) {
		this.servicePubKey = servicePubKey;
	}

}
