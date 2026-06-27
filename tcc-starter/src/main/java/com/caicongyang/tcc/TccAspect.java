package com.caicongyang.tcc;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AOP aspect that intercepts @TccTransaction methods.
 * <ul>
 *   <li>Begins a TCC transaction before the Try phase.</li>
 *   <li>If Try succeeds, calls the Confirm method on the proxy.</li>
 *   <li>If Try throws, calls the Cancel method on the proxy.</li>
 *   <li>Cleans up context regardless of outcome.</li>
 * </ul>
 */
@Aspect
public class TccAspect {

    private static final Logger log = LoggerFactory.getLogger(TccAspect.class);

    private final TccCoordinator coordinator;

    public TccAspect(TccCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Around("@annotation(com.caicongyang.tcc.TccTransaction)")
    public Object aroundTccTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        TccTransaction annotation = method.getAnnotation(TccTransaction.class);

        // Begin transaction
        TransactionContext ctx = coordinator.begin();
        Object result = null;

        Throwable tryException = null;
        try {
            // Try phase
            result = joinPoint.proceed();
        } catch (Throwable e) {
            tryException = e;
        }

        // Call confirm/cancel on the proxy so Spring-managed state (fields, AOP) stays consistent
        Object proxy = joinPoint.getThis();

        if (tryException == null) {
            // Success — execute Confirm if specified
            if (!annotation.confirmMethod().isEmpty()) {
                try {
                    invokePhase(proxy, annotation.confirmMethod());
                } catch (Exception e) {
                    log.error("TCC confirm method [{}] failed for transaction [{}]",
                            annotation.confirmMethod(), ctx.getTransactionId(), e);
                }
            }
            coordinator.confirm(ctx);
        } else {
            // Failure — execute Cancel if specified
            log.warn("TCC try method [{}] failed for transaction [{}], executing cancel",
                    method.getName(), ctx.getTransactionId(), tryException);
            if (!annotation.cancelMethod().isEmpty()) {
                try {
                    invokePhase(proxy, annotation.cancelMethod());
                } catch (Exception e) {
                    log.error("TCC cancel method [{}] failed for transaction [{}]",
                            annotation.cancelMethod(), ctx.getTransactionId(), e);
                }
            }
            coordinator.cancel(ctx);
        }

        coordinator.cleanup(ctx);

        if (tryException != null) {
            throw tryException;
        }
        return result;
    }

    /**
     * Invoke a named no-arg method on the given object via reflection.
     */
    private void invokePhase(Object target, String methodName) throws Exception {
        Method method = target.getClass().getMethod(methodName);
        method.invoke(target);
    }
}
