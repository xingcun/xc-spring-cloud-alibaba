package com.xc.service;

import com.alibaba.fastjson.JSONObject;

public interface TestService {
	
	
	public String testDubbo(JSONObject obj);

	public String getServiceCache(String key);


	public void testRemoteEvent();

	public void testLocalEvent(String msg);

	public String testMqEvent( String name,Integer age);
	
}
