package com.kve.master.config.response;

public enum SysExceptionEnum implements ExceptionEnumInterface {

    /**
     * 异常枚举
     */
    OK(0, "操作成功"),
    INVALID_PARAM(3000, "参数校验错误"),

    USER_NOT_EXIST(4001, "用户不存在"),
    USER_PASSWORD_ERROR(4002, "用户名或密码错误"),
    USER_ACCESS_DENIED(4004, "访问权限不足"),
    USER_IS_LOCK(4005, "用户已锁定"),
    USER_IS_DISABLED(4006, "用户已停用"),
    USER_LOGIN_ERROR(4007, "登录异常"),
    SAME_USER_NAME_EXISTS(4008, "用户名{0}已经存在"),

    PROJECT_NOT_EXISTS(4009, "项目projectKey参数未配置"),
    PARAM_ILLEGAL(4010, "参数不合法"),
    PARAM_CORN_ILLEGAL(4011, "时间表达式错误"),
    TASK_GROUP_THE_SAME_EXISTS(4012, "任务组{0}下任务{1}.{2}已经存在"),
    TASK_NOT_EXISTS(4013, "任务不存在"),
    JOB_CLASS_NOT_EXISTS(4014, "名称为{0}任务类不存在"),
    JOB_CLASS_METHOD_NOT_EXISTS(4015, "任务类{0}方法{1}不存在或参数个数不匹配"),
    SAVE_NOT_NEED_NOT_UPDATE(4016, "您未修改任何信息，无需保存"),
    FAIL_SCHEDULER(4017, "调度错误"),
    JOB_IS_RUN(4018, "任务已启动"),
    JOB_IS_STOP(4019, "任务已停止"),
    FAIL_JOB_IS_RUNNING(4020, "任务运行中,禁止修改"),
    FAIL_JOB_FINISH(40021, "任务运行结束,禁止修改"),

    SYSTEM_NOT_LOGIN_ERROR(5000, "未登录 ，请先登录"),
    SYSTEM_ERROR(6000, "网络繁忙，请稍后再试"),

    ;

    SysExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;

    private String msg;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}