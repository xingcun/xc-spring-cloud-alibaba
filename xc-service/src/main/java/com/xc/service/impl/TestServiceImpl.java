package com.xc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xc.event.XcLocalEvent;
import com.xc.event.XcRemoteEvent;
import com.xc.util.CacheFactory;
import com.xc.vo.MessageVo;
import com.xc.vo.ModelVo;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.xc.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.UUID;


@Service
@org.springframework.stereotype.Service
@Transactional
public class TestServiceImpl implements TestService{

	@Value("${spring.cloud.bus.id}")
	private String originService;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private Source source;


	@Override
	public String testDubbo(JSONObject obj) {
		RpcContext rpcContext = RpcContext.getContext();
		if(obj!=null) {
			System.out.println(obj.toJSONString());
		}
		System.out.println("============================"+ String.format("Service [name :TestServiceImpl , port : %d] %s: Hello, %s",
               0,// rpcContext.getLocalPort(),
               "",// rpcContext.getMethodName(),
                "xc-service"
                ));
		return null;
	}

	@Override
	public String getServiceCache(String key) {
		System.out.println("key:"+key);
		String msg =(String) CacheFactory.getInstance().getTestCache().get(key);
		System.out.println("cache msg:"+msg);
		return msg;
	}

	@Override
	public void testRemoteEvent() {
		XcRemoteEvent event = new XcRemoteEvent(this,originService,null);
		MessageVo message = new MessageVo();
		message.setSubject("xc-test-subject");
		JSONObject obj = new JSONObject();
		obj.put("id", UUID.randomUUID().toString());
		message.setContent(obj);
		event.setMessageVo(message);
		applicationContext.publishEvent(event);
	}

	@Override
	public void testLocalEvent(String msg) {
		XcLocalEvent event = new XcLocalEvent(this,msg);
		applicationContext.publishEvent(event);
	}

	@Override
	public String testMqEvent( String name,Integer age) {
		MessageVo message = new MessageVo();
		message.setSubject("mq-message-subject");
		JSONObject obj = new JSONObject();
		obj.put("name",name);
		obj.put("age",age);
		message.setContent(obj);
		source.output().send(MessageBuilder.withPayload(message).build());
		return "send User payload message success";
	}
}
