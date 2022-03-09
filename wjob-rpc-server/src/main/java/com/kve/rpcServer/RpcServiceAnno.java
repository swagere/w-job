package com.kve.rpcServer;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义服务发布注解：
 * 1. spring管理
 * 2. 被发布成对外服务接口
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component // 使用此注解的类，能被spring扫描
public @interface RpcServiceAnno {
    Class<?> value(); //定义使用此注解的服务接口名称
}