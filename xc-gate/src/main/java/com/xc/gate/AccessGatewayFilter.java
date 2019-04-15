package com.xc.gate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSONObject;
import com.xc.util.jwt.JWTInfo;
import com.xc.vo.BaseModelVo;
import com.xc.vo.BaseModelVo.Code;
import com.xc.vo.CommonVariable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class AccessGatewayFilter implements GlobalFilter {

	@Value("${ignore.startWith}")
	private String startWith;


	private static final String GATE_WAY_PREFIX = "/api";

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, GatewayFilterChain gatewayFilterChain) {

		LinkedHashSet requiredAttribute = serverWebExchange
				.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
		ServerHttpRequest request = serverWebExchange.getRequest();
		String requestUri = request.getPath().pathWithinApplication().value();
		if (requiredAttribute != null) {
			Iterator<URI> iterator = requiredAttribute.iterator();
			while (iterator.hasNext()) {
				URI next = iterator.next();
				if (next.getPath().startsWith(GATE_WAY_PREFIX)) {
					requestUri = next.getPath().substring(GATE_WAY_PREFIX.length());
				}
			}
		}
		final String method = request.getMethod().toString();
		ServerHttpRequest.Builder mutate = request.mutate();
		// 不进行拦截的地址
		if (isStartWith(requestUri)) {
			ServerHttpRequest build = mutate.build();
			return gatewayFilterChain.filter(serverWebExchange.mutate().request(build).build());
		}
		JWTInfo user = null;
		try {
			user = getJWTUser(request, mutate);
		} catch (Exception e) {
			BaseModelVo vo = new BaseModelVo();
			vo.setCodeEnum(Code.ERROR, e.getMessage());
			return getVoidMono(serverWebExchange, vo);
		}
		ServerHttpRequest build = mutate.build();
		return gatewayFilterChain.filter(serverWebExchange.mutate().request(build).build());

	}

	/**
	 * 网关抛异常
	 *
	 * @param body
	 */
	private Mono<Void> getVoidMono(ServerWebExchange serverWebExchange, BaseModelVo body) {
		serverWebExchange.getResponse().setStatusCode(HttpStatus.OK);
		byte[] bytes = JSONObject.toJSONString(body).getBytes(StandardCharsets.UTF_8);
		DataBuffer buffer = serverWebExchange.getResponse().bufferFactory().wrap(bytes);
		return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
	}

	/**
	 * 返回session中的用户信息
	 *
	 * @param request
	 * @param ctx
	 * @return
	 */
	private JWTInfo getJWTUser(ServerHttpRequest request, ServerHttpRequest.Builder ctx) throws Exception {
		List<String> strings = request.getHeaders().get(CommonVariable.REQUEST_HEAD_TOKEN);
		String authToken = null;
		if (strings != null) {
			authToken = strings.get(0);
		}

		ctx.header(CommonVariable.REQUEST_HEAD_TOKEN, authToken);
		return null;
	}

	/**
	 * URI是否以什么打头
	 *
	 * @param requestUri
	 * @return
	 */
	private boolean isStartWith(String requestUri) {
		boolean flag = false;
		for (String s : startWith.split(",")) {
			if (requestUri.startsWith(s)) {
				return true;
			}
		}
		return flag;
	}


}
