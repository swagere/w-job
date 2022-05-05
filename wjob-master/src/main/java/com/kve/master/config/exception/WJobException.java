package com.kve.master.config.exception;


import com.kve.master.config.response.ExceptionEnumInterface;

/**
 * 业务异常
 */
public class WJobException extends BaseException {

    public WJobException(String message) {
        super(message);
    }

    public WJobException(Integer code, String message) {
        super(code, message);
    }

    public WJobException(ExceptionEnumInterface enums, Object... args) {
        super(enums.getCode(), enums.getMsg(), args);
    }

}