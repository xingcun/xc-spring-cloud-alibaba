package com.xc.service.control;

import com.alibaba.fastjson.JSONObject;
import com.xc.quartz.DynamicJob;
import com.xc.service.impl.TestServiceImpl;
import com.xc.util.SpringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.Set;

@RestController
public class TestFeignControl {

    @Autowired
    private TestServiceImpl testService;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/testRest")
    public String testRest(String msg){
        ServiceInstance serviceInstance = loadBalancerClient.choose("xc-admin");
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/testDubbo";
        System.out.println(url);
        return restTemplate.getForObject(url, String.class,msg);
    }




    @RequestMapping(value = "/testHystrix")
    public JSONObject testHystrix(String msg) {
        System.out.println("service-------------++++++ "+msg+"++++++++++-----------------testHystrix");
        JSONObject obj = new JSONObject();
        obj.put("status", 1);
        if (new Random().nextInt(10) % 2 == 0) {
            /*测试熔断*/
            try {
                Thread.sleep(8000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return obj;
    }

    @RequestMapping(value = "/testHystrixFactory")
    public JSONObject testHystrixFactory(String msg) {
        System.out.println("service-------------"+msg+"-----------------testHystrixFactory");
        JSONObject obj = new JSONObject();
        obj.put("status", 1);
        if (new Random().nextInt(10) % 2 == 0) {
            /*测试熔断*/
            try {
                Thread.sleep(8000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else{
            //throw  new RuntimeException("程序运行异常，已经删库跑路了");
        }
        return obj;
    }



}
