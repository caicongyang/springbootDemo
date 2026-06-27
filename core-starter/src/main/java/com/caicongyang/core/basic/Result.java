package com.caicongyang.core.basic;

import com.caicongyang.core.basic.enums.CodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 定义基础的返回体
 *
 * @author caicongyang
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 8583536407729865387L;

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(CodeEnum.Success.value());
        result.setMessage(CodeEnum.Success.desc());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(CodeEnum.Failed.value());
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> fail(Throwable e) {
        return fail(e.getMessage());
    }

    @Schema(description = "返回状态码")
    private Integer code = CodeEnum.Success.value();

    @Schema(description = "返回状态信息")
    private String message = CodeEnum.Success.desc();

    @Schema(description = "返回是否成功")
    private Boolean success;

    private T data;

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
}
