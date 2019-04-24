package com.xc.service.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xc.pojo.user.User;

public class Test {
	public static void main(String[] args) throws InterruptedException {
		JSONObject obj = new JSONObject();
		obj.put("username","xingcun");
		obj.put("orgId","22");
		obj.put("source", "3");
		User user = obj.toJavaObject(User.class);
		
		JSONObject userObj = (JSONObject) JSON.toJSON(user);
		System.out.println(userObj.get("source").getClass());
		if(userObj.get("username") instanceof String) {
			System.out.println("000000000000000000000000000000000000000000");
		}
		long time = System.currentTimeMillis()/1000;
		Thread.sleep(3000L);
		System.out.println( System.currentTimeMillis()/1000-time);
	}
}
