package com.xc.admin.feign;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class TestFeignHystrix implements TestFeign {

	@Override
	public JSONObject test(JSONObject obj) {
		System.out.println("任务熔断:"+obj.toJSONString());
		obj.clear();
		obj.put("message", "任务熔断");
		return obj;
	}

}
