package com.xc.admin.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "xc-service",fallbackFactory=TestFeignHystrixFactory.class)
public interface TestFeignFactory {
    @RequestMapping(value = "/testHystrixFactory")
    public JSONObject testHystrixFactory(@RequestParam String msg);
}
