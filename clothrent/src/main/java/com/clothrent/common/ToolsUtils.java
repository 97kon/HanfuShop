package com.clothrent.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clothrent.entity.SysUser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by liuqiming
 */
public class ToolsUtils {

    private static  final Logger logger= LoggerFactory.getLogger(ToolsUtils.class);


    //计算课节数
    public static String calculateCourseNum(LocalDateTime beginTime, LocalDateTime endTime){

        LocalTime checkBegin = beginTime.toLocalTime();
        // 开始时间点
        LocalTime localTime1=LocalTime.parse("08:20:00");// 第1小节开始时间
        LocalTime localTime1End=LocalTime.parse("09:05:00");// 第1小节结束时间
        LocalTime localTime2=LocalTime.parse("09:15:00");// 第2小节开始时间
        LocalTime localTime2End=LocalTime.parse("10:00:00");// 第2小节结束时间
        LocalTime localTime3=LocalTime.parse("10:20:00");// 第3小节开始时间
        LocalTime localTime3End=LocalTime.parse("11:05:00");// 第3小节结束时间
        LocalTime localTime4=LocalTime.parse("11:15:00");// 第4小节开始时间
        LocalTime localTime4End=LocalTime.parse("12:00:00");// 第4小节结束时间

        LocalTime localTime5=LocalTime.parse("14:30:00");// 第5小节开始时间
        LocalTime localTime5End=LocalTime.parse("15:15:00");// 第5小节结束时间
        LocalTime localTime6=LocalTime.parse("15:25:00");// 第6小节开始时间
        LocalTime localTime6End=LocalTime.parse("16:10:00");// 第6小节结束时间
        LocalTime localTime7=LocalTime.parse("16:30:00");// 第7小节开始时间
        LocalTime localTime7End=LocalTime.parse("17:15:00");// 第7小节结束时间
        LocalTime localTime8=LocalTime.parse("17:25:00");// 第8小节开始时间
        LocalTime localTime8End=LocalTime.parse("18:10:00");// 第8小节结束时间

        LocalTime localTime9=LocalTime.parse("19:20:00");// 第9小节开始时间
        LocalTime localTime9End=LocalTime.parse("20:05:00");// 第9小节结束时间
        LocalTime localTime10=LocalTime.parse("20:15:00");// 第10小节开始时间
        LocalTime localTime10End=LocalTime.parse("21:00:00");// 第10小节结束时间

        int begin=1;
        if(checkBegin.compareTo(localTime9End)>=0){
            begin=10;
        }else if(checkBegin.compareTo(localTime8End)>=0){
            begin=9;
        }else if(checkBegin.compareTo(localTime7End)>=0){
            begin=8;
        }else if(checkBegin.compareTo(localTime6End)>=0){
            begin=7;
        }else if(checkBegin.compareTo(localTime5End)>=0){
            begin=6;
        }else if(checkBegin.compareTo(localTime4End)>=0){
            begin=5;
        }else if(checkBegin.compareTo(localTime3End)>=0){
            begin=4;
        }else if(checkBegin.compareTo(localTime2End)>=0){
            begin=3;
        }else if(checkBegin.compareTo(localTime1End)>=0){
            begin=2;
        }

        // 计算截止课节数
        LocalTime checkEnd = endTime.toLocalTime();
        int end=1;

        if(checkEnd.compareTo(localTime10)>=0){
            end=10;
        }else if(checkEnd.compareTo(localTime9)>=0){
            end=9;
        }else if(checkEnd.compareTo(localTime8)>=0){
            end=8;
        }else if(checkEnd.compareTo(localTime7)>=0){
            end=7;
        }else if(checkEnd.compareTo(localTime6)>=0){
            end=6;
        }else if(checkEnd.compareTo(localTime5)>=0){
            end=5;
        }else if(checkEnd.compareTo(localTime4)>=0){
            end=4;
        }else if(checkEnd.compareTo(localTime3)>=0){
            end=3;
        }else if(checkEnd.compareTo(localTime2)>=0){
            end=2;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(begin).append(begin==end?"":",");

        if(begin+1==end){
            stringBuilder.append(end);
        }
        if(begin+1<end){
            stringBuilder.append(begin+1).append(",").append(end);
        }
//        stringBuilder.append(begin).append(begin+1==end?"":begin+1).append(end);

        return stringBuilder.toString();

    }



    // 比如 1.5万  返回 15000
    public static Float handleNumber(String source){
        String regex="[\\u4e00-\\u9fa5]+";
        String goal = RegexUtil.parseGoalFromStr(source, regex);
        if(StringUtils.isEmpty(goal)){//没有中文
            return  Float.parseFloat(source);
        }
        String[] split = source.split(goal);
        if(goal.equals("万")){
            return Float.parseFloat(split[0])*10000;
        }else if(goal.equals("亿")){
            return Float.parseFloat(split[0])*100000000;
        }
        return 0F;
    }

    //封装返回结果--针对layui table
    public static Map wrapperPageInfo(IPage listPage){
        Map<String, Object> pageInfo = new HashMap<String, Object>();//
        pageInfo.put("pageSize", listPage.getSize());//每页大小
        pageInfo.put("pageNum", listPage.getCurrent());//当前第几页
        pageInfo.put("pages", listPage.getPages());//总页数
        pageInfo.put("rows", listPage.getRecords());//查询的记录列表
        pageInfo.put("total", listPage.getTotal());//总记录数
        return pageInfo;
    }
    //封装返回结果
    public static Map wrapperResult(IPage listPage,String listCode){
        Map<String, Object> pageInfo = new HashMap<String, Object>();
        pageInfo.put("pageSize", listPage.getSize());
        pageInfo.put("pageNum", listPage.getCurrent());
        pageInfo.put("pages", listPage.getPages());
        pageInfo.put("total", listPage.getTotal());
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(listCode, listPage.getRecords());
        resultMap.put("pageInfo", pageInfo);
        return resultMap;
    }

    /**
     * 从Excel中获取单元格数据，返回格式为String
     * @param cell
     * @return
     */
    public static String getValFromCell(Cell cell){
        if(cell==null){
            return null;
        }
        CellType cellTypeEnum = cell.getCellType();
        switch (cellTypeEnum) {
            case NUMERIC:return String.valueOf(cell.getNumericCellValue());
            case STRING:return cell.getStringCellValue();
            case BOOLEAN:return String.valueOf(cell.getBooleanCellValue());
            case ERROR:return String.valueOf(cell.getErrorCellValue());
            case FORMULA:
            case _NONE:
            case BLANK:return null;
            default:
                return null;
        }
    }

    //从cell获取日期类型值
    public static String getDateCellVal(Cell cell,String pattern){
        if(cell==null){
            return null;
        }
        Date value = cell.getDateCellValue();
        return DateUtils.format(value,pattern);
    }

    public static SysUser getLoginUser(HttpSession session){
        Object user = session.getAttribute("user");

        return user==null?null:(SysUser) user;
    }

    /**
     * 获取登录用户ID
     * @param session
     * @return
     */
    public static Long getLoginUserId(HttpSession session){
        Object user = session.getAttribute("user");
        if(user==null){
           return  null;
        }else{
            SysUser loginUser= (SysUser) user;
            return loginUser.getId();
        }
    }

    /**
     * 获取登录用户名称
     * @param session
     * @return
     */
    public static String getLoginUserName(HttpSession session){
        Object user = session.getAttribute("user");
        if(user==null){
           return null;
        }else{
            SysUser loginUser= (SysUser) user;
            return loginUser.getName();
        }
    }

    //驼峰转下划线
    public static String camelToUnderline(String fieldName){
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<fieldName.length();i++){
            char c = fieldName.charAt(i);
            //65-90是大写A到大写Z 97到122是小写a到小写z。
            if(i!=0&&i!=fieldName.length()-1&&c>=65&&c<=90){
                //说明是大写且非首末字母则需要转驼峰
                char lowerCase = Character.toLowerCase(c);
                stringBuffer.append("_").append(lowerCase);
            }else{
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }

    //对象转map且字段转换为下划线格式
    public static Map<String,Object> convertObjToMap(Object obj){
        if(obj == null){
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>();

        List excludeFields=new ArrayList();
        excludeFields.add("serialVersionUID");
       //只取属性字段
       for (Field declaredField : obj.getClass().getDeclaredFields()) {
            try {
                declaredField.setAccessible(true);
                String fieldName = declaredField.getName();
                if(!excludeFields.contains(fieldName)){
                    map.put(camelToUnderline(fieldName), declaredField.get(obj));
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(),e);
            }
        }
//        //只取含有get方法的
//        List<String> listName = new ArrayList<>();
//        for (Field declaredField : obj.getClass().getDeclaredFields()) {
//            listName.add(declaredField.getName());
//        }
//        for (Method declaredMethod : obj.getClass().getDeclaredMethods()) {
//            if (declaredMethod.getName().startsWith("get")) {
//                String name = (String.valueOf(declaredMethod.getName().charAt(3)).toLowerCase())+declaredMethod.getName().substring(4);
//                try {
//                    if(listName.contains(name)){
//                        map.put(name, declaredMethod.invoke(obj));
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return map;
    }
    //对象转map且字段转换为下划线格式
    public static Map<String,Object> convertObjToMap(Object obj,Boolean isNull){
        if(obj == null){
            return Collections.emptyMap();
        }
        if(isNull==null){
            isNull=true;
        }
        Map<String, Object> map = new HashMap<>();

        List excludeFields=new ArrayList();
        excludeFields.add("serialVersionUID");
       //只取属性字段
       for (Field declaredField : obj.getClass().getDeclaredFields()) {
            try {
                declaredField.setAccessible(true);
                String fieldName = declaredField.getName();
                if(!excludeFields.contains(fieldName)){
                    Object value = declaredField.get(obj);
                    if(!isNull){
                        if(value!=null){
                            map.put(camelToUnderline(fieldName),value );
                        }
                    }else{
                        map.put(camelToUnderline(fieldName),value );
                    }
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return map;
    }

    /**
     * collection 是否为空
     * @param collection
     * @return true:is empty ;false:not empty
     */
    public static boolean isEmpty(Collection collection){
       return !isNotEmpty(collection);
    }
    /**
     * collection 是否不为空
     * @param collection
     * @return true:is empty ;false:not empty
     */
    public static boolean isNotEmpty(Collection collection){
        if(collection==null||collection.isEmpty()){
            return false;
        }else{
            return  true;
        }
    }

    //返回订单号
    public static  String createOrderNum(){
        String yyyyMMddHHmmss = DateUtils.format(new Date(), "yyyyMMddHHmmss");
        int i = new Random().nextInt(1000) + 1000;
        return "M"+yyyyMMddHHmmss+i;
    }

    /**
     * 将百分数转换为BigDecimal
     * @param str e.g:10%
     * @return 0.1
     */
    public static BigDecimal stringToBigDecimal(String str){
        if(str==null){
            logger.debug("str is null");
            return null;
        }
        if(str.equals("0")||str.equals("0%")){
            return new BigDecimal(0);
        }
        //左闭右开
        String substring = str.substring(0, str.length() - 1);
        BigDecimal bigDecimal=new BigDecimal(substring);
        BigDecimal divide = bigDecimal.divide(new BigDecimal(100));
        logger.debug(str+" 转换后的结果为："+divide.doubleValue());
        return divide;
    }

    /**
     * 动态获取object的field值
     * @param Object
     * @param field
     * @return
     */
    public static String getValueFromObj(Object obj, String field) {
        for (Field declaredField : obj.getClass().getDeclaredFields()) {
            try {
                declaredField.setAccessible(true);
                String fieldName = declaredField.getName();
                if(fieldName.equals(field)){
                    return String.valueOf(declaredField.get(obj));
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(),e);
                return null;
            }
        }
        return null;
    }
}
