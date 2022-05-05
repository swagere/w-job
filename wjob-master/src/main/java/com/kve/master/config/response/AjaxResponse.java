package com.kve.master.config.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一返回对象
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjaxResponse implements Serializable {

    private static final long serialVersionUID = -1L;
    private static final String EMPTY = "";

    /**
     * 编码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回的的数据
     */
    private Object data;

    private Integer count;

    public AjaxResponse(Object data, Integer code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    /**
     * 成功请求
     * success : true
     * errorCode : 默认 2000
     * errorMsg : 默认 ""
     *
     * @return JobAdminResponse
     */
    public static AjaxResponse success() {
        return success(null);
    }

    /**
     * 成功请求
     * success : true
     * errorCode : 默认 2000
     * errorMsg : 默认 ""
     *
     * @param data obj参数
     * @return JobAdminResponse
     */
    public static AjaxResponse success(Object data) {
        return new AjaxResponse(data, SysExceptionEnum.OK.getCode(), EMPTY);
    }

    public static AjaxResponse error(Integer errorCode, String errorMsg) {
        return new AjaxResponse(null, errorCode, errorMsg);
    }

}
