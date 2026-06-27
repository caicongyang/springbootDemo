package com.caicongyang.core.aspect;

import com.caicongyang.core.domain.RequestErrorInfo;
import com.caicongyang.core.domain.RequestInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志切面 — 记录请求参数、响应结果、耗时和异常信息
 */
@Aspect
public class RequestLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogAspect.class);

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.PostMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.RequestMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.PutMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long timeCost = System.currentTimeMillis() - start;

        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return result;
            }
            HttpServletRequest request = attributes.getRequest();

            RequestInfo info = new RequestInfo();
            info.setIp(getClientIp(request));
            info.setUrl(request.getRequestURL().toString());
            info.setHttpMethod(request.getMethod());
            info.setClassMethod(classMethod(joinPoint));
            info.setRequestParams(getRequestParams(joinPoint));
            info.setResult(result);
            info.setTimeCost(timeCost);

            if (timeCost > 3000) {
                LOGGER.warn("SLOW-REQUEST {} {} {}ms", request.getMethod(), info.getUrl(), timeCost);
            } else {
                LOGGER.info("REQUEST {} {} {}ms", request.getMethod(), info.getUrl(), timeCost);
            }
        } catch (Exception e) {
            LOGGER.debug("记录请求日志失败", e);
        }
        return result;
    }

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, Exception e) {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            RequestErrorInfo errorInfo = new RequestErrorInfo();
            errorInfo.setIp(getClientIp(request));
            errorInfo.setUrl(request.getRequestURL().toString());
            errorInfo.setHttpMethod(request.getMethod());
            errorInfo.setClassMethod(classMethod(joinPoint));
            errorInfo.setRequestParams(getRequestParams(joinPoint));

            if (e instanceof RuntimeException re) {
                errorInfo.setException(re);
            } else {
                errorInfo.setException(new RuntimeException(e));
            }

            LOGGER.error("REQUEST-ERROR {} {}: {}", request.getMethod(), errorInfo.getUrl(),
                    e.getMessage(), e);
        } catch (Exception ex) {
            LOGGER.debug("记录异常日志失败", ex);
        }
    }

    private Map<String, Object> getRequestParams(JoinPoint joinPoint) {
        Map<String, Object> params = new HashMap<>();
        String[] paramNames = resolveParameterNames(joinPoint);
        Object[] paramValues = joinPoint.getArgs();
        if (paramNames == null || paramValues == null) {
            return params;
        }
        for (int i = 0; i < Math.min(paramNames.length, paramValues.length); i++) {
            Object value = paramValues[i];
            if (value instanceof MultipartFile file) {
                params.put(paramNames[i], file.getOriginalFilename());
            } else if (value instanceof HttpServletRequest req) {
                params.put(paramNames[i], req.getRequestURL().toString());
            } else {
                params.put(paramNames[i], value);
            }
        }
        return params;
    }

    /**
     * 解析方法参数名。优先从 java.lang.reflect.Parameter 获取（需要 -parameters 编译选项），
     * 其次尝试从 MethodSignature 获取（AspectJ 织入后可能保留），降级使用 argN。
     */
    private String[] resolveParameterNames(JoinPoint joinPoint) {
        if (joinPoint.getSignature() instanceof MethodSignature sig) {
            Method method = sig.getMethod();
            Parameter[] parameters = method.getParameters();
            if (parameters.length > 0 && parameters[0].isNamePresent()) {
                String[] names = new String[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    names[i] = parameters[i].getName();
                }
                return names;
            }
            // fallback: MethodSignature might have parameter names from AspectJ
            String[] aspectNames = sig.getParameterNames();
            if (aspectNames != null && aspectNames.length > 0) {
                return aspectNames;
            }
        }
        // ultimate fallback
        Object[] args = joinPoint.getArgs();
        if (args == null) return new String[0];
        String[] fallback = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            fallback[i] = "arg" + i;
        }
        return fallback;
    }

    private String classMethod(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringTypeName()
                + "." + joinPoint.getSignature().getName();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
