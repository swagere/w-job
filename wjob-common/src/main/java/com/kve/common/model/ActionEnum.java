package com.kve.common.model;

public enum ActionEnum {
    RUN("RUN", "启动/恢复"),
    PAUSE("PAUSE", "暂停"),
    STOP("STOP", "停止"),
    BEAT("BEAT", "心跳检测")
    ;

    private String value;
    private String name;

    ActionEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
