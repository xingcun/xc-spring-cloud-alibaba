package com.xc.service.app;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;


@EnableDubbo(scanBasePackages = "com.xc.service.impl")
//@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = "com.xc")
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "com.xc.dao")

@EntityScan("com.xc.pojo")
@RemoteApplicationEventScan(basePackages = "com.xc")
@EnableBinding(Source.class)
public class XcServiceBootstrap {


	public static void main(String[] args) {
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//		context.register(XcServiceBootstrap.class);
//		context.refresh();
//		  new SpringApplicationBuilder(XcServiceBootstrap.class).run(args);
		ConfigurableApplicationContext context = SpringApplication.run(XcServiceBootstrap.class, args);
		String[] beans = context.getBeanDefinitionNames();

		for (String bean : beans)

		{

			System.out.println(bean + " of Type :: " + context.getBean(bean).getClass());

		}
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

	/*
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
	*/

	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
		// 1.需要定义一个convert转换消息的对象;
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		// 2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
				SerializerFeature.DisableCircularReferenceDetect);
		fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
		// 3处理中文乱码问题
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		// 4.在convert中添加配置信息.
		fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
		fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
		HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;
		return new HttpMessageConverters(converter);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
