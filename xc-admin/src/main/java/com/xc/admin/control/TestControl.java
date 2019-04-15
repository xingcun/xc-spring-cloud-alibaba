package com.xc.admin.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.xc.admin.feign.TestFeign;
import com.xc.service.TestCacosService;
import com.xc.service.TestService;
import com.xc.util.CacheFactory;

@RestController
public class TestControl {

	@Reference
	private TestService testService;

	@Reference
	private TestCacosService testCacosService;

	@Autowired
	private TestFeign testFeign;
	
	@RequestMapping(value = "/test")
	public JSONObject test(String msg) {
		System.out.println("------------------------------");
		CacheFactory.getInstance().getCache("test").put("msg", msg);
		JSONObject obj = new JSONObject();
		testService.test();
		testCacosService.send(msg);
		obj.put("status", 1);
		return obj;
	}
	
	@RequestMapping(value = "/testFeign")
	public JSONObject testFeign(@RequestBody JSONObject obj) {
		System.out.println("admin cache msg:"+CacheFactory.getInstance().getCache("test").get("msg"));
		return testFeign.test(obj);
	}

}
