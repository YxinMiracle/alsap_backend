package com.yxinmiracle.alsap.common;

/**
 * 自定义错误码
 *

 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    ILLEGALITY_REQUEST_ERROR(40400, "不合法的请求"),
    TIME_REQUEST_ERROR(40500, "您电脑时间与北京时间不一致，请重新设置一致后，刷新页面即可正常显示!"),
    AI_SERVER_ERROR(40600, "AI服务请求异常"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    EMPTY_DATA(50002, "数据为空");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
