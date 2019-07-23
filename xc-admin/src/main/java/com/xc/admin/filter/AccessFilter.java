package com.xc.admin.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.xc.vo.BaseModelVo;
import com.xc.vo.ModelVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.xc.exception.NeedLoginException;
import com.xc.util.CommonUtil;
import com.xc.util.LoginUserHolder;
import com.xc.vo.CommonVariable;
import org.springframework.util.AntPathMatcher;

@Configuration
public class AccessFilter implements Filter {

	@Value("${ignore.startWith}")
	private String startWith;
	private AntPathMatcher matcher = new AntPathMatcher();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String requestUri = req.getRequestURI();

		// 不进行拦截的地址
		if (isStartWith(requestUri)) {
			chain.doFilter(request, response);
			return;
		}

		//String token = req.getHeader(CommonVariable.REQUEST_HEAD_TOKEN);

        if (LoginUserHolder.getLoginUser()!=null) {

        	chain.doFilter(request, response);
        	return;
        }else {
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
			ModelVo vo = new ModelVo();
			vo.setCodeEnum(BaseModelVo.Code.USER_NO_LOGIN);
			response.getWriter().println(JSON.toJSONString(vo));
        }
//        ctx.header(userAuthConfig.getTokenHeader(), authToken);
//        BaseContextHandler.setToken(authToken);
//        return userAuthUtil.getInfoFromToken(authToken);
	}

	/**
	   * 是否以什么打头
	 *
	 * @param requestUri
	 * @return
	 */
	private boolean isStartWith(String requestUri) {
		if(startWith==null || startWith.length()==0) {
			return false;
		}
		if(requestUri.startsWith("/favicon.ico")) {
			return true;
		}
		for (String url : startWith.split(",")) {
			if (matcher.match(url,requestUri)) {
				return true;
			}
		}
		return false;
	}

}
