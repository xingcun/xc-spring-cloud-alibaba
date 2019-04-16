package com.xc.service.app;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

@EnableDubbo(scanBasePackages = "com.xc.service.impl")
//@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = "com.xc")
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "com.xc.dao")

@EntityScan("com.xc.pojo")
public class XcServiceBootstrap {

	@Value("${spring.cloud.nacos.config.server-addr}")
	private String serverAddr;
	
	@Value("${sentinel.flow-rule}")
	private String sentinelFlowRule;

	public static void main(String[] args) {
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//		context.register(XcServiceBootstrap.class);
//		context.refresh();
//		  new SpringApplicationBuilder(XcServiceBootstrap.class).run(args); 
		SpringApplication.run(XcServiceBootstrap.class, args);
		System.out.println("XcServiceBootstrap provider is starting...");
		List<Ignite> ignites = Ignition.allGrids();
		for (Ignite ignite : ignites) {
			System.out.println(ignite.name());
		}

	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	public ReadableDataSource<String, List<FlowRule>> getReadableDataSource() {
		ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(serverAddr,
				"DEFAULT_GROUP", // "Sentinel:Demo",
				sentinelFlowRule,
				source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
				}));
		FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
		return flowRuleDataSource;
	}
}
