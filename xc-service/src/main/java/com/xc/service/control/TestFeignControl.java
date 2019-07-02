package com.xc.service.control;

import com.alibaba.fastjson.JSONObject;
import com.xc.service.impl.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class TestFeignControl {

    @Autowired
    private TestServiceImpl testService;





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
