package com.xc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xc.service.TestCacosService;
import com.xc.util.CacheFactory;

@Service
public class TestCacosServiceImpl implements TestCacosService{

	@Override
	public String send(String msg) {
		System.out.println("msg:"+msg);
		System.out.println("cache msg:"+CacheFactory.getInstance().getCache("test").get("msg"));
		return msg;
	}

}
