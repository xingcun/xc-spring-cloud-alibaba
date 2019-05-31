package com.xc.admin.feign;

import com.alibaba.fastjson.JSON;
import com.xc.vo.BaseModelVo;
import com.xc.vo.ModelVo;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class TestFeignHystrix implements TestFeign {

	@Override
	public JSONObject testHystrix(String msg) {
		System.out.println("任务熔断:"+msg);
		ModelVo vo = new ModelVo();
		vo.setCodeEnum(BaseModelVo.Code.ERROR,"任务失败,熔断触发成功:"+msg);
		return (JSONObject)JSON.toJSON(vo);
	}

}
