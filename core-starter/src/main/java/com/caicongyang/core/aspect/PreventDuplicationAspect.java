package com.caicongyang.core.aspect;

import com.caicongyang.core.annotation.PreventDuplication;
import com.caicongyang.core.exception.BusinessException;
import com.caicongyang.core.utils.IPUtil;
import com.caicongyang.core.utils.JacksonUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Aspect
public class PreventDuplicationAspect {

    @Resource
    private RedissonClient redissonClient;

    private static final String KEY_PREFIX = "PREVENT_DUPLICATION";

    @Pointcut("@annotation(com.odianyun.project.support.aop.annotation.PreventDuplication)")
    public void preventDuplication() {
    }

    @Around("preventDuplication()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Assert.notNull(attributes, "ServletRequestAttributes cannot be null.");
        HttpServletRequest request = attributes.getRequest();
        Assert.notNull(request, "request cannot be null.");

        //String userName = SessionHelper.getUsername();
        // TODO
        String userName = StringUtils.EMPTY;
        if (StringUtils.isEmpty(userName)) {
            userName = IPUtil.getIpAddr(request);
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        PreventDuplication annotation = method.getAnnotation(PreventDuplication.class);
        //生成redis的key：PREVENT_DUPLICATION:登陆账号:请求URI:SHA1(方法名+请求参数)
        String redisKey = "PREVENT_DUPLICATION:" + userName + ":" + request.getRequestURI() + ":" + getMethodSign(method, joinPoint.getArgs());
        //使用incr防止并发
        RAtomicLong atomicLong = redissonClient.getAtomicLong(redisKey);
        Long incr = atomicLong.incrementAndGet();
        if (Objects.equals(incr, 1L)) {
            atomicLong.expire(annotation.expireSeconds(), TimeUnit.SECONDS);
            try {
                return joinPoint.proceed();
            } catch (Throwable t) {
                redissonClient.getBucket(redisKey).delete();
                throw t;
            }
        } else {
            throw new BusinessException(-1,"请勿重复提交");
        }
    }

    /**
     * 生成方法标记：采用数字签名算法SHA1对方法签名字符串加签
     * @param method
     * @param args
     * @return
     */
    private String getMethodSign(Method method, Object... args) {
        StringBuilder sb = new StringBuilder(method.toString());
        for (Object arg : args) {
            if (Objects.isNull(arg)) {
                sb.append("null");
            } else {
                sb.append(JacksonUtils.jsonFromObject(arg));
            }
        }
        return DigestUtils.sha1DigestAsHex(sb.toString());
    }
}
