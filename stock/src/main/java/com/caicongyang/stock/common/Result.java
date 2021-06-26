package com.caicongyang.stock.common;


import com.caicongyang.stock.enums.CodeEnum;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


/**
 * 定义基础的返回体
 *
 * @author caicongyang
 * @version $Id: BaseRep.java, v 0.1 2016年3月9日 下午2:52:04 caicongyang Exp $
 */
public class Result<T> implements Serializable {

    /**  */
    private static final long serialVersionUID = 8583536407729865387L;


    public static Result ok(Object T) {
        Result result = new Result();
        result.setCode(CodeEnum.Success.value());
        result.setMessage(CodeEnum.Success.desc());
        result.setData(T);
        return result;
    }

    public static Result ok() {
        return ok(null);
    }

    public static Result fail(String message) {
        Result result = new Result();
        result.setCode(CodeEnum.Success.value());
        result.setMessage(message);
        return result;
    }

    public static Result fail(Throwable e) {
        return fail(e.getMessage());
    }


    /**
     * @CodeEnum
     */
    @ApiModelProperty(value = "返回状态码")
    private Integer code = CodeEnum.Success.value();
    /**
     * 接口返回text
     */
    @ApiModelProperty(value = "返回状态信息")
    private String message = CodeEnum.Success.desc();

    @ApiModelProperty(value = "返回是否成功")
    private Boolean success;
    /**
     * 返回的数据
     */
    private T data;

    /**
     * Getter method for property <tt>code</tt>.
     *
     * @return property value of code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Setter method for property <tt>code</tt>.
     *
     * @param code value to be assigned to property code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * Getter method for property <tt>message</tt>.
     *
     * @return property value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter method for property <tt>message</tt>.
     *
     * @param message value to be assigned to property message
     */
    public void setMessage(String message) {
        this.message = message;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
