package com.kve.master.util;

import java.security.MessageDigest;

public class EncodeUtil {

    //MD5加密
    public static String MD5Util(String data) {

        //获得java提供信息摘要算法加密功能类的一个实例
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        //将获取到的string转换成byte数组
        char[] chars = data.toCharArray();
        byte[] bytes = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }

        //获取MD5计算后的byte值
        byte[] md5byte = md5.digest(bytes);
        //将获取到的byte值转回string
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < md5byte.length; i++) {
            //使用0xff保持转值不出错
            int val = ((int) md5byte[i]) & 0xff;
            if (val < 16) {
                stringBuffer.append("0");
            } else {
                stringBuffer.append(Integer.toHexString(val));
            }
        }

        return stringBuffer.toString();
    }
}