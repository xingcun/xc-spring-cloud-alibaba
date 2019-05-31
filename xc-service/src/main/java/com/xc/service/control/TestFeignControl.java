package com.xc.service.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xc.service.impl.TestServiceImpl;

@RestController
public class TestFeignControl {

	@Autowired
	private TestServiceImpl testService;


	@RequestMapping(value = "/testMsg")
	public JSONObject test(@RequestBody JSONObject obj) {
		System.out.println("service------------------------------TestControl");
		testService.send(obj.toJSONString());
		obj.put("serviceName","xc-service");
		obj.put("status", 1);
//		if(1==1) {
//			throw new RuntimeException("+++++++++++++++++++++++++++++++++++");
//		}
		/*
		 测试熔断
		try {
			Thread.sleep(8000L);
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

	@RequestMapping(value = "/testRemoteEvent")
	public JSONObject testRemoteEvent() {
		System.out.println("service-------------+++++++++++++++++++++-----------------testRemoteEvent");
		JSONObject obj = new JSONObject();
		testService.testRemoteEvent();
		obj.put("status", 1);
		return obj;
	}

	@RequestMapping(value = "/testSendObj")
	public JSONObject testSendObj(String name,Integer age) {
		System.out.println("service-------------+++++++++++++++++++++-----------------testRemoteEvent");
		JSONObject obj = new JSONObject();
		obj.put("message",testService.sendObj(name,age));
		obj.put("status", 1);
		return obj;
	}
}
