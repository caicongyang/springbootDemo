package com.caicongyang.core.filter;

import com.caicongyang.core.exception.BusinessException;
import java.lang.reflect.Method;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.service.GenericService;

/**
 * @author caicongyang
 */
@Activate(group = CommonConstants.PROVIDER)
public class DubboProviderExceptionFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(DubboProviderExceptionFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result appResponse = invoker.invoke(invocation);
        if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
            try {
                Throwable exception = appResponse.getException();

                // directly throw if it's checked exception
                if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                    return appResponse;
                }
                // directly throw if it's checked exception
                if (exception instanceof BusinessException) {
                    return appResponse;
                }
                // directly throw if the exception appears in the signature
                try {
                    Method method = invoker.getInterface()
                        .getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    Class<?>[] exceptionClassses = method.getExceptionTypes();
                    for (Class<?> exceptionClass : exceptionClassses) {
                        if (exception.getClass().equals(exceptionClass)) {
                            return appResponse;
                        }
                    }
                } catch (NoSuchMethodException e) {
                    return appResponse;
                }

                // for the exception not found in method's signature, print ERROR message in server's log.
                logger.error("Got unchecked and undeclared exception which called by " + RpcContext
                    .getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName()
                    + ", method: " + invocation.getMethodName() + ", exception: " + exception
                    .getClass().getName() + ": " + exception.getMessage(), exception);

                // directly throw if exception class and interface class are in the same jar file.
                String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                if (serviceFile == null || exceptionFile == null || serviceFile
                    .equals(exceptionFile)) {
                    return appResponse;
                }
                // directly throw if it's JDK exception
                String className = exception.getClass().getName();
                if (className.startsWith("java.") || className.startsWith("javax.")) {
                    return appResponse;
                }
                // directly throw if it's dubbo exception
                if (exception instanceof RpcException) {
                    return appResponse;
                }

                // otherwise, wrap with RuntimeException and throw back to the client
                appResponse.setException(new RuntimeException(StringUtils.toString(exception)));
                return appResponse;
            } catch (Throwable e) {
                logger.warn("Fail to ExceptionFilter when called by " + RpcContext.getContext()
                    .getRemoteHost() + ". service: " + invoker.getInterface().getName()
                    + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass()
                    .getName() + ": " + e.getMessage(), e);
                return appResponse;
            }
        }
        return appResponse;
    }
}
