package com.caicongyang.core.aspect;

import com.caicongyang.core.domain.RequestErrorInfo;
import com.caicongyang.core.domain.RequestInfo;
import com.caicongyang.core.utils.jacksonUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;


/**
 * 日志切面
 *
 * @author caicongyang 需要启用AspectJ动态代理 @EnableAspectJAutoProxy
 *
 * 如何把@EnableAspectJAutoProxy 放在starter 中
 */
@Aspect
public class RequestLogAspect {


    private final static Logger LOGGER = LoggerFactory.getLogger(RequestLogAspect.class);


    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)"
        + "|| @annotation(org.springframework.web.bind.annotation.PostMapping)"
        + "|| @annotation(org.springframework.web.bind.annotation.RequestMapping)"
        + "|| @annotation(org.springframework.web.bind.annotation.PutMapping)"
        + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object result = proceedingJoinPoint.proceed();
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setIp(request.getRemoteAddr());
        requestInfo.setUrl(request.getRequestURL().toString());
        requestInfo.setHttpMethod(request.getMethod());
        requestInfo.setClassMethod(
            String.format("%s.%s", proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                proceedingJoinPoint.getSignature().getName()));
        requestInfo.setRequestParams(getRequestParamsByProceedingJoinPoint(proceedingJoinPoint));
        requestInfo.setResult(result);
        requestInfo.setTimeCost(System.currentTimeMillis() - start);
        LOGGER.info("Request Info      : {}", jacksonUtils.jsonFromObject(requestInfo));

        return result;
    }


    @AfterThrowing(value = "@annotation(org.springframework.web.bind.annotation.GetMapping)"
        + "|| @annotation(org.springframework.web.bind.annotation.PostMapping)"
        + "|| @annotation(org.springframework.web.bind.annotation.RequestMapping)"
        + "|| @annotation(org.springframework.web.bind.annotation.PutMapping)"
        + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping))", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, RuntimeException e) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        RequestErrorInfo requestErrorInfo = new RequestErrorInfo();
        requestErrorInfo.setIp(request.getRemoteAddr());
        requestErrorInfo.setUrl(request.getRequestURL().toString());
        requestErrorInfo.setHttpMethod(request.getMethod());
        requestErrorInfo
            .setClassMethod(String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()));
        requestErrorInfo.setRequestParams(getRequestParamsByJoinPoint(joinPoint));
        requestErrorInfo.setException(e);
        LOGGER.info("Error Request Info      : {}", jacksonUtils.jsonFromObject(requestErrorInfo));
    }

    /**
     * 获取入参
     */
    private Map<String, Object> getRequestParamsByProceedingJoinPoint(
        ProceedingJoinPoint proceedingJoinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature) proceedingJoinPoint.getSignature())
            .getParameterNames();
        //参数值
        Object[] paramValues = proceedingJoinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> getRequestParamsByJoinPoint(JoinPoint joinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = joinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValues) {
        Map<String, Object> requestParams = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValues[i];

            //如果是文件对象
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                //获取文件名
                value = file.getOriginalFilename();
            }

            requestParams.put(paramNames[i], value);
        }

        return requestParams;
    }
}
