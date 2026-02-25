package com.bilibili.domain.exception;

import java.io.Serializable;

/**
 * @author: Dk.L
 * @date: 2023/3/20
 * @Description: 条件异常类
 */
public class ConditionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String code;

    public ConditionException(String code, String name){
        super(name);
        this.code = code;
    }

    public ConditionException(String name){
        super(name);
        code = "500";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
