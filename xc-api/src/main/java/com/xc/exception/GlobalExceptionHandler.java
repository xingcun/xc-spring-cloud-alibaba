package com.xc.exception;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xc.vo.BaseModelVo;
import com.xc.vo.BaseModelVo.Code;

@ControllerAdvice()
@ResponseBody
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    public BaseModelVo otherExceptionHandler(HttpServletResponse response, Exception ex) {
        response.setStatus(500);
        logger.error(ex.getMessage(),ex);
        BaseModelVo vo = new BaseModelVo();
        if(ex instanceof NeedLoginException) {
        	vo.setCodeEnum(Code.USER_NO_LOGIN, ex.getMessage());
        }else if(ex.getMessage()!=null && ex.getMessage().contains("FlowException")) {
        	vo.setCodeEnum(Code.SUPER_EXCEPTION, "访问量过高,请稍后访问");
        }else {
        	vo.setCodeEnum(Code.SUPER_EXCEPTION, ex.getMessage());
        }
        return vo;
    }

}
