package com.clothrent.common;

/**
 * Created  at 2024/7/31.
 * 返回结果包装类：封装了一些静态方法
 */
public class ResultUtil {

    public static ResponseBean success(CommonEnum commonEnum){
        return new ResponseBean(true,null,commonEnum);
    }
    public static ResponseBean error(CommonEnum commonEnum){
        return new ResponseBean(false,null,commonEnum);
    }
    public static ResponseBean success( Object data, String msg){
        return new ResponseBean(true,data,CommonEnum.SUCCESS_OPTION.getCode(),msg);
    }
    public static ResponseBean successData( Object data){
        return new ResponseBean(true,data,CommonEnum.SUCCESS_OPTION);
    }
    public static ResponseBean success( String msg){
        return new ResponseBean(true,null,CommonEnum.SUCCESS_OPTION.getCode(),msg);
    }
    public static ResponseBean success( ){
        return new ResponseBean(true,null,CommonEnum.SUCCESS_OPTION);
    }
    public static ResponseBean error( Object data, String msg){
        return new ResponseBean(false,data,CommonEnum.SYSTEM_ERROR.getCode(),msg);
    }
    public static ResponseBean errorData( Object data){
        return new ResponseBean(false,data,CommonEnum.SYSTEM_ERROR);
    }
    public static ResponseBean error( String msg){
        return new ResponseBean(false,null,CommonEnum.SYSTEM_ERROR.getCode(),msg);
    }
    public static ResponseBean error( ){
        return new ResponseBean(false,null,CommonEnum.SYSTEM_ERROR);
    }
}
