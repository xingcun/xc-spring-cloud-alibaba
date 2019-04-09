package com.xc.exception;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xc.vo.ModelVo;
import com.xc.vo.ModelVo.Code;

@ControllerAdvice()
@ResponseBody
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    public ModelVo otherExceptionHandler(HttpServletResponse response, Exception ex) {
        response.setStatus(500);
        logger.error(ex.getMessage(),ex);
        ModelVo vo = new ModelVo();
        if(ex instanceof NeedLoginException) {
        	vo.setCodeEnum(Code.USER_NO_LOGIN, ex.getMessage());
        }else {
        	vo.setCodeEnum(Code.SUPER_EXCEPTION, ex.getMessage());
        }
        return vo;
    }

}
