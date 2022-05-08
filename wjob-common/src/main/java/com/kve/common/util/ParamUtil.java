package com.kve.common.util;

import org.springframework.util.StringUtils;

public class ParamUtil {
    /**
     * 处理Job设置的参数多个使用分隔，推荐使用单个String 类型JSON参数
     */
    public static Object[] getJobArgs(String methodArgs) {
        //参数处理
        Object[] args = null;
        if (!StringUtils.isEmpty(methodArgs)) {
            methodArgs = methodArgs + " ";
            String[] argString = methodArgs.split("&&");
            args = new Object[argString.length];
            for (int i = 0; i < argString.length; i++) {
                args[i] = argString[i].trim();
            }
        }
        return args;
    }

    /**
     * 处理参数
     */
    public static Class[] getParameters(Object[] jobArs) {
        if (jobArs == null) {
            return null;
        }
        Class[] parameterType = null;
        parameterType = new Class[jobArs.length];
        for (int i = 0; i < jobArs.length; i++) {
            parameterType[i] = String.class;
        }
        return parameterType;
    }
}
