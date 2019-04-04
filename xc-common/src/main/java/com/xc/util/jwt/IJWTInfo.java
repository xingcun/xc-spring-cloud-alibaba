package com.xc.util.jwt;

import java.util.Map;

/**
 * Created by ace on 2017/9/10.
 */
public interface IJWTInfo extends Map {
	/**
	 * 获取用户名
	 * 
	 * @return
	 */
	String getUsername();

	/**
	 * 获取用户ID
	 * 
	 * @return
	 */
	String getId();

	/**
	 * 获取名称
	 * 
	 * @return
	 */
	String getNickName();
}
