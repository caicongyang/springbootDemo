/**
 * u51.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.caicongyang.stock.enums;

import com.google.common.base.Preconditions;

/**
 * 返回码
 *
 * @author ilike
 * @version $Id: CodeEnum.java, v 0.1 2016年3月9日 下午2:53:33 ilike Exp $
 */
public enum CodeEnum {

    /**
     * 失败
     */
    Failed(1, "Failed"),
    /**
     * 成功
     */
    Success(0, "Success");

    private int value;
    private String desc;

    CodeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * Getter method for property <tt>value</tt>.
     *
     * @return property value of value
     */
    public int value() {
        return value;
    }

    /**
     * Getter method for property <tt>desc</tt>.
     *
     * @return property value of desc
     */
    public String desc() {
        return desc;
    }

    /**
     * 根据值转为枚举
     *
     * @param value
     * @return
     */
    public static CodeEnum fromValue(Integer value) {
        Preconditions.checkArgument(null != value && value >= 0, "value invalid");
        if (null != value && value >= 0) {
            for (CodeEnum item : CodeEnum.values()) {
                if (value.equals(item.value())) {
                    return item;
                }
            }
        }
        return null;
    }
}
