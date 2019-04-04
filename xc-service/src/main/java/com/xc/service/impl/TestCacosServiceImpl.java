package com.xc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xc.service.TestCacosService;

@Service
public class TestCacosServiceImpl implements TestCacosService{

	@Override
	public String send(String msg) {
		System.out.println("msg:"+msg);
		return msg;
	}

}
