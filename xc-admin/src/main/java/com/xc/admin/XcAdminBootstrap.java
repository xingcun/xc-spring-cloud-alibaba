package com.xc.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.xc.admin.schedule.TestSchedule;
import com.xc.exception.GlobalExceptionHandler;
import com.xc.service.user.UserService;
import com.xc.util.LoginUserHolder;
import com.xc.util.SpringUtils;

@EnableDubbo(scanBasePackages="com.xc.*")
//@EnableAutoConfiguration
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class,scanBasePackages= {"com.xc"})
@EnableFeignClients({ "com.xc.admin.feign" })
@EnableDiscoveryClient
@Configuration
@EnableScheduling
@EnableBinding(Sink.class)
@RemoteApplicationEventScan(basePackages = "com.xc")
public class XcAdminBootstrap implements WebMvcConfigurer,ErrorPageRegistrar   {

	@Value("${xc.ignite.config:applicationContext-ignite.xml}")
	private String ignitePath;
	
	
	public static void main(String[] args) {
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//		context.register(XcServiceBootstrap.class);
//		context.refresh();
//		  new SpringApplicationBuilder(XcServiceBootstrap.class).run(args);
//		 SpringApplication springApplication = new SpringApplication(XcAdminBootstrap.class);
//	        springApplication.addListeners(new LoginUserHolder());
//	        springApplication.run(args);
	
		ConfigurableApplicationContext context = SpringApplication.run(XcAdminBootstrap.class, args);
	/*	
		 String[] beans = context.getBeanDefinitionNames();

	        for (String bean : beans)

	        {

	            System.out.println(bean + " of Type :: " + context.getBean(bean).getClass());

	        }
	        */
		System.out.println("XcAdminBootstrap provider is starting...");
	}

	@Bean
	public Ignite getIgnite() {
		return Ignition.start(ignitePath);
	}
	
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
	public GlobalExceptionHandler getGlobalExceptionHandler() {
		return new GlobalExceptionHandler();
	}

	@Override
	public void registerErrorPages(ErrorPageRegistry registry) {
		registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR,"/commomError"),new ErrorPage(HttpStatus.NOT_FOUND,"/404"));
	}

	public String getIgnitePath() {
		return ignitePath;
	}

	public void setIgnitePath(String ignitePath) {
		this.ignitePath = ignitePath;
	}

}
