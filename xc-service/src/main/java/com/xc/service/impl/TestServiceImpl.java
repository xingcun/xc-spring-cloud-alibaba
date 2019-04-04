package com.xc.service.impl;

import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.xc.service.TestService;


@Service
public class TestServiceImpl implements TestService{

    @Value("${demo.service.name}")
    private String serviceName;
    
	@Override
	public String test() {
		RpcContext rpcContext = RpcContext.getContext();
		
		System.out.println("============================"+ String.format("Service [name :TestServiceImpl , port : %d] %s: Hello, %s",
                rpcContext.getLocalPort(),
                rpcContext.getMethodName(),
                serviceName
                ));
		return null;
	}

}
