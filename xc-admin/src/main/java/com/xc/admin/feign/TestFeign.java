package com.xc.admin.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

@FeignClient(value = "xc-service",fallback=TestFeignHystrix.class)
//@FeignClient(value = "xc-service")
public interface TestFeign {
	
	@RequestMapping(value = "/testMsg",method = RequestMethod.POST)
	public JSONObject test(@RequestBody JSONObject obj);
	
}
