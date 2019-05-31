package com.xc.admin.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "xc-service",fallback=TestFeignHystrix.class)
//@FeignClient(value = "xc-service")
public interface TestFeign {
	
	@RequestMapping(value = "/testHystrix")
	public JSONObject testHystrix(@RequestParam  String msg);
	
}
