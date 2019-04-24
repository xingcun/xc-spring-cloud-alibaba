package com.xc.service.impl;

import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.xc.service.TestService;


@Service
@org.springframework.stereotype.Service
@Transactional
public class TestServiceImpl implements TestService{

    @Value("${demo.service.name}")
    private String serviceName;
    
	@Override
	public String test() {
		RpcContext rpcContext = RpcContext.getContext();
		
		System.out.println("============================"+ String.format("Service [name :TestServiceImpl , port : %d] %s: Hello, %s",
               "",// rpcContext.getLocalPort(),
               "",// rpcContext.getMethodName(),
                serviceName
                ));
		return null;
	}

}
