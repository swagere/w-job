package com.kve.master.util;

import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.List;

/**
 * @version BeanCopierUtil.java
 */
public class BeanCopyUtil {

    /**
     * 对象属性拷贝
     *
     * @param source      来源
     * @param targetClass 目标
     * @param <T>         目标类型
     * @return 值
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        T t = null;
        try {
            t = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Create new instance of %s failed: %s");
        }
        copyProperty(source, t);
        return t;
    }

    /**
     * 对象集合属性拷贝
     *
     * @param orig 来源
     * @param dest 目标
     * @param <T>  目标类型
     * @return 值
     */
    public static <T> List<T> copyList(List orig, Class<T> dest) {
        try {
            List<T> resultList = new ArrayList<>();
            if (orig == null || orig.size() <= 0) {
                return resultList;
            }
            for (Object o : orig) {
                T destObject = dest.newInstance();
                if (o == null) {
                    continue;
                }
                copyProperty(o, destObject);
                resultList.add(destObject);
            }
            return resultList;
        } catch (Exception e) {
            return null;
        }
    }

    private static void copyProperty(Object source, Object target) {
        BeanCopier copier = BeanCopier.create(source.getClass(), target.getClass(), false);
        copier.copy(source, target, null);
    }

}
