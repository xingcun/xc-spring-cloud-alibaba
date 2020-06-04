package com.ht.project.exception;

import com.ht.project.common.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice()
@ResponseBody
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    public JsonResult otherExceptionHandler(HttpServletResponse response, Exception ex) {
        response.setStatus(500);
        logger.error(ex.getMessage(),ex);
        JsonResult result = new JsonResult();
        if(ex instanceof HttpMediaTypeNotSupportedException) {
            return result.setErrorCode(JsonResult.Code.ERROR,"http请求方式不对");
        }
        if(ex instanceof NeedLoginException) {
            result.setErrorCode(JsonResult.Code.USER_NO_LOGIN,ex.getMessage());
        }else if(ex.getMessage()!=null && ex.getMessage().contains("FlowException")) {
            result.setErrorCode(JsonResult.Code.FLOW_LIMIT);
        }else {
            result.setErrorCode(JsonResult.Code.SUPER_EXCEPTION, ex.getMessage());
        }
        return result;
    }

}
