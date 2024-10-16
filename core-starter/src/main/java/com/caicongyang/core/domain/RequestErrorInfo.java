package com.caicongyang.core.domain;

import java.io.Serializable;

/**
 * @author caicongyang
 */
public class RequestErrorInfo  implements Serializable {


    private static final long serialVersionUID = -1065819998590764630L;
    private String ip;
    private String url;
    private String httpMethod;
    private String classMethod;
    private Object requestParams;
    private RuntimeException exception;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getClassMethod() {
        return classMethod;
    }

    public void setClassMethod(String classMethod) {
        this.classMethod = classMethod;
    }

    public Object getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Object requestParams) {
        this.requestParams = requestParams;
    }

    public RuntimeException getException() {
        return exception;
    }

    public void setException(RuntimeException exception) {
        this.exception = exception;
    }
}
