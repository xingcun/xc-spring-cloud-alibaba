package com.xc.service.impl;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xc.service.TestCacosService;
import com.xc.util.CacheFactory;

@Service
@org.springframework.stereotype.Service
@Transactional
public class TestCacosServiceImpl implements TestCacosService{

	@Override
	public String send(String msg) {
		System.out.println("msg:"+msg);
		System.out.println("cache msg:"+CacheFactory.getInstance().getCache("test").get("msg"));
		return msg;
	}

}
