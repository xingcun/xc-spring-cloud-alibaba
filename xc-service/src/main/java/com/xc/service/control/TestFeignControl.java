package com.xc.service.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xc.service.TestCacosService;
import com.xc.service.TestService;
import com.xc.service.impl.TestCacosServiceImpl;
import com.xc.service.impl.TestServiceImpl;

@RestController
public class TestFeignControl {

	@Autowired
	private TestServiceImpl testService;

	@Autowired
	private TestCacosServiceImpl testCacosService;
	
	
    
    
    
	@RequestMapping(value = "/testMsg")
	public JSONObject test(@RequestBody JSONObject obj) {
		System.out.println("service------------------------------TestControl");
		testCacosService.send(obj.toJSONString());
		obj.put("serviceName","xc-service");
		obj.put("status", 1);
//		if(1==1) {
//			throw new RuntimeException("+++++++++++++++++++++++++++++++++++");
//		}
		/*
		 测试熔断
		ry {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		return obj;
	}

	
	@RequestMapping(value = "/test")
	public JSONObject test() {
		System.out.println("service-------------+++++++++++++++++++++-----------------TestControl");
		JSONObject obj = new JSONObject();
		testService.test();
		obj.put("status", 1);
		return obj;
	}
}
