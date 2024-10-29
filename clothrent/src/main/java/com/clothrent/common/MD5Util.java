package com.clothrent.common;

import org.springframework.util.DigestUtils;

public class MD5Util {

    public static String md5(String text, String key) {
        //加密后的字符串
        String encodeStr= DigestUtils.md5DigestAsHex((text + key).getBytes());
//        System.out.println("MD5加密后的字符串为:encodeStr="+encodeStr);
        return encodeStr;
    }

    public static String md5(String text) {
        //加密后的字符串
        String encodeStr= DigestUtils.md5DigestAsHex((text).getBytes());
//        System.out.println("MD5加密后的字符串为:encodeStr="+encodeStr);
        return encodeStr;
    }

    public static boolean md5Verify(String text, String key, String md5) {
        //根据传入的密钥进行验证
        String md5Text = md5(text, key);
        if(md5Text.equalsIgnoreCase(md5))
        {
            return true;
        }

        return false;
    }

    public static boolean md5Verify(String text, String md5){
        //根据传入的密钥进行验证
        String md5Text = md5(text);
        if(md5Text.equalsIgnoreCase(md5)){
//            System.out.println("MD5验证通过");
            return true;
        }

        return false;
    }
    public static void main(String[] args){
    
        System.out.println(md5("123456","clothrent"));
    }
}
