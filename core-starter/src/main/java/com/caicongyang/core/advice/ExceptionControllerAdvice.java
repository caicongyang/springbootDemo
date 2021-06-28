package com.caicongyang.core.advice;

import com.caicongyang.core.basic.Result;
import com.caicongyang.core.exception.BusinessException;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * @author caicongyang
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);


    /**
     * 处理受检异常，没被捕获的异常都会被这个方法捕获
     */
    @ExceptionHandler(value = Exception.class)
    public Result<String> handleException(Exception e, HttpServletResponse response) {
        logger.error("全局异常拦截非捕获异常-", e);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            //404 Not Found
            return Result.fail(404, "请求未找到，请稍后重试！");
        }
        if (e instanceof java.lang.reflect.UndeclaredThrowableException) {
            //500
            return Result.fail(500, "系统异常，请稍后重试！");
        }
        return Result.fail("系统异常，请稍后重试！");
    }



    /**
     * 可以将Exception中的异常信息以Json的信息返回给前端
     *
     * @param e 自定义异常
     * @return JSON message为 throw new BusinessException("消息体") 中的消息体
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        logger.error("全局异常拦截捕获异常BusinessException-", e);
        return Result.fail(e.getErrCode(),e.getMessage());
    }
}
