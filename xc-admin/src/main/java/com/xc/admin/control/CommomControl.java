package com.xc.admin.control;

import javax.servlet.http.HttpServletRequest;

import org.omg.CORBA.ServerRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xc.exception.NeedLoginException;
import com.xc.vo.ModelVo;
import com.xc.vo.ModelVo.Code;

@RestController
public class CommomControl {
	
//	private final ErrorAttributes errorAttributes;
//	
//	public CommomControl(ErrorAttributes errorAttributes) {
//		this.errorAttributes = errorAttributes;
//	}
	
	@RequestMapping("/404")
	public ModelVo notFound() {
		ModelVo vo = new ModelVo();
		vo.setCodeEnum(Code.NO_FOUND);
		return vo;
	}
	
	
	@RequestMapping("/commomError")
	@ResponseBody
	public ModelVo error(HttpServletRequest request) {
		ModelVo vo = new ModelVo();
		Throwable exception  = (Throwable) request.getAttribute("javax.servlet.error.exception");
		if(exception!=null) {
			 if(exception instanceof NeedLoginException) {
		        	vo.setCodeEnum(Code.USER_NO_LOGIN, exception.getMessage());
		        }else {
		        	vo.setCodeEnum(Code.SUPER_EXCEPTION, exception.getMessage());
		        }
		}else {
			vo.setCodeEnum(Code.SUPER_EXCEPTION);
		}
		return vo;
	}
	
}
