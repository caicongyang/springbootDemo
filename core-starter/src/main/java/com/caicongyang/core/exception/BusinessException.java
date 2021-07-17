package com.caicongyang.core.exception;


/**
 * @author caicongyang
 */
public class BusinessException extends RuntimeException {


    /**
     * <p>Constructor for BusinessException.</p>
     */
    public BusinessException() {
    }

    /**
     * 异常代码
     */
    public Integer errCode;
    /**
     * 异常提示，和exception的msg字段区分开。
     */
    public String errMsg;


    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    /**
     * <p>Getter for the field <code>errMsg</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getErrMsg() {
        return errMsg;
    }


    /**
     * <p>Constructor for BusinessException.</p>
     *
     * @param code a {@link String} object.
     * @param msg a {@link String} object.
     */
    public BusinessException(Integer code, String msg) {
        super(msg);
        this.errCode = code;
        this.errMsg = msg;
    }

    /**
     * <p>Constructor for BusinessException.</p>
     *
     * @param code a {@link String} object.
     * @param msg a {@link String} object.
     * @param cause a {@link Throwable} object.
     */
    public BusinessException(Integer code, String msg, Throwable cause) {
        super(msg, cause);
        this.errCode = code;
        this.errMsg = msg;
    }
}
