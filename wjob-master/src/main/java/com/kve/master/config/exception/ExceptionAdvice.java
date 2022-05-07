package com.kve.master.config.exception;

import com.kve.master.config.response.AjaxResponse;
import com.kve.master.config.response.SysExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局统一异常处理
 *
 */
@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public AjaxResponse defaultExceptionHandler(Exception exception) {
        AjaxResponse result = new AjaxResponse();
        try {
            throw exception;
        } catch (WJobException e) {
            log.error("[ 全局异常捕获 ] >> 自定义业务异常  >> errorMsg : {} ", e.getMsg());
            log.debug("[ 全局异常捕获 ] >> 自定义业务异常堆栈  >> stack : ", e);
            result.setCode(e.getCode());
            result.setMsg(e.getMessage());
        } catch (HttpRequestMethodNotSupportedException e) {
            String errorMsg = String.format("请求方式 %s 错误 ! 请使用 %s 方式", e.getMethod(), e.getSupportedHttpMethods());
            log.error("[ 全局异常捕获 ] >> {}", errorMsg);
            result.setCode(SysExceptionEnum.INVALID_PARAM.getCode());
            result.setMsg(errorMsg);
        } catch (HttpMediaTypeNotSupportedException e) {
            String errorMsg = String.format("请求类型 %s 错误 ! 请使用 %s 方式", e.getContentType(), e.getSupportedMediaTypes());
            log.error("[ 全局异常捕获 ] >> {}", errorMsg);
            result.setCode(SysExceptionEnum.INVALID_PARAM.getCode());
            result.setMsg(errorMsg);
        } catch (Exception e) {
            log.error("[ 全局异常捕获 ] >>  未知异常 stack :", e);
            result.setCode(SysExceptionEnum.SYSTEM_ERROR.getCode());
            result.setMsg(SysExceptionEnum.SYSTEM_ERROR.getMsg());
        }
        return result;
    }

}