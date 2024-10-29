package com.clothrent.common;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created  at 2024/9/19.
 * 正则工具类
 */
public class RegexUtil {

    public static Double parseDoubleFromUnit(String source){
        Integer measure=1;
        if(source.contains("万")){
            source=source.replaceAll("万","");
            measure=10000;
        }else if(source.contains("千")){
            source=source.replaceAll("千","");
            measure=1000;
        }else if(source.contains("百万")){
            source=source.replaceAll("百万","");
            measure=1000000;
        }

        Double v = Double.parseDouble(source) * measure;
        return v;
    }

    // 解析字符串中的目标对象
    public static String parseGoalFromStr(String source,String regex){
        if(StringUtils.isEmpty(source)){
            return source;
        }
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(source);
        StringBuilder stringBuilder=new StringBuilder();
        while(matcher.find()) {
            String group = matcher.group();
            stringBuilder.append(group).append(",");
        }
        String string = stringBuilder.toString();
        return string==null||string.isEmpty()?null:string.substring(0,string.length()-1);
    }

    // 解析字符串中的数字
    public static String parseNumFromStr(String source){
        String regex="\\d+";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(source);
        StringBuilder stringBuilder=new StringBuilder();
        while(matcher.find()) {
            String group = matcher.group();
            stringBuilder.append(group).append(",");
        }
        String string = stringBuilder.toString();
        return string.substring(0,string.length()-1);
    }

    /**
     * 校验名字里面是否有特殊字符
     * @param str
     * @return
     */
    public static boolean checkSpecial(String str){
        String regEx="^.*[/`\\\\~!@#$%^&*()+=|{}':;,\\[\\]<>?！￥…（）【】‘；：”“’。，、？]+.*$";
        return Pattern.matches(regEx, str);
    }

    /**
     * 校验不能对以"SUPER_"开头的操作员名称进行操作
     * @param str
     * @return
     */
    public static boolean checkSUPER(String str){
        String regEx="SUPER_";
        return str.startsWith(regEx);
    }

    /**
     * 校验身份证号
     * @param value
     * @return
     */
    public static boolean checkCardNo(String value){
        String regEx="(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
        return Pattern.matches(regEx, value);
    }

    /**
     *校验是否为正整数
     * @param str
     * @return true:正整数；false：非正整数
     */
    public static boolean isPositiveNum(String str){
        String regEx="^[1-9]+\\d*$";
        return Pattern.matches(regEx, str);
    }

    /**
     * 校验是否为手机号
     * @param mobile
     * @return true 手机号
     */
    public static boolean checkMobile(String mobile) {
        String regex = "(\\+\\d+)?1[345789]\\d{9}$";
        return Pattern.matches(regex,mobile);
    }

    /**
     * 校验是否为邮箱
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return Pattern.matches(regex, email);
    }

    /**
     * 校验字符串长度
     * @param
     * @return true 长度在范围内
     */
    public static boolean checkStrlen(String str,int n,int m) {
        String regex = "^.{"+n+","+m+"}$";
        return Pattern.matches(regex,str);
    }
    /**
     * 校验字符串长度
     * @param
     * @return true 长度在范围内
     */
    public static boolean checkStrlen(String str,int m) {
        String regex = "^.{0"+","+m+"}$";
        return Pattern.matches(regex,str);
    }


    public static void main(String[] args){
        String str = "1234";
        boolean matches = RegexUtil.checkStrlen(str,2);
        System.out.println(str +" , 校验结果 ："+matches);
    }
}
