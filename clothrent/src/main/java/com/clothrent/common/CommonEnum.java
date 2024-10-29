package com.clothrent.common;

public enum CommonEnum {

    SUCCESS_REQUEST("0000","请求成功"),
    SUCCESS_OPTION("0000","操作成功！"),
    LOGIN_SUCCESS("0000","登录成功"),
    LOGOUT_SUCCESS("0000","已退出登录"),
    SYSTEM_ERROR("9999","系统异常"),
    BAD_REQUEST("0001","错误的请求"),
    BAD_PARAM("0002","参数错误"),
    NO_USER_EXIST("0003","用户不存在"),
    INVALID_PASSWORD("0004","密码错误"),
    USER_CODE_EXIST("0005","账号已经存在"),
    NOT_MATCH("0006","用户名和密码不匹配"),
    REFRESH_VERIFYCODE("0007","请刷新验证码"),
    ERROR_VERIFYCODE("0008","验证码错误"),
    INVALID_MOBILE("0010","无效的手机号码"),
    NO_RECORD("0016","没有查到相关记录");

    private String code;

    private String msg;

    CommonEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
