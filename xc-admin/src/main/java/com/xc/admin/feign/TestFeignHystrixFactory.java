package com.xc.admin.feign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xc.vo.BaseModelVo;
import com.xc.vo.ModelVo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class TestFeignHystrixFactory implements FallbackFactory<TestFeignFactory> {

    @Override
    public TestFeignFactory create(Throwable throwable) {
        //此处为TestFeignFactory的实现类，由于只有一个方法，所以使用简写
        return (msg)-> {
            throwable.printStackTrace();
            ModelVo vo = new ModelVo();
            vo.setCodeEnum(BaseModelVo.Code.ERROR,msg+"任务失败,熔断工厂触发成功:"+throwable.getMessage());
            return (JSONObject) JSON.toJSON(vo);
        };
    }
}
