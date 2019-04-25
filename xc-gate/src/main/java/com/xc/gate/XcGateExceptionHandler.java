package com.xc.gate;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.fastjson.JSONObject;
import com.xc.vo.BaseModelVo;
import com.xc.vo.BaseModelVo.Code;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class XcGateExceptionHandler implements WebExceptionHandler {


	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		exchange.getResponse().setStatusCode(HttpStatus.OK);
		byte[] bytes = JSONObject.toJSONString(buildErrorResult(ex)).getBytes(StandardCharsets.UTF_8);
		DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
		return exchange.getResponse().writeWith(Flux.just(buffer));
		
	}


	private BaseModelVo buildErrorResult(Throwable ex) {
		BaseModelVo vo = new BaseModelVo();
		if(ex instanceof FlowException) {
			vo.setCodeEnum(Code.ERROR,"访问量过高,请稍候访问");
		}else {
			vo.setCodeEnum(Code.ERROR, ex.getClass().getSimpleName() + ":" + ex.getMessage());
		}
		return vo;
	}


}
