package com.kve.master.config.response;

/**
 * @author mengq
 */
public interface ExceptionEnumInterface {

    /**
     * 提示码
     *
     * @return code
     */
    Integer getCode();

    /**
     * 提示信息
     *
     * @return msg
     */
    String getMsg();
}