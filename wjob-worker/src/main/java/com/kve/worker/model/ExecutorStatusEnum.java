package com.kve.worker.model;

public enum ExecutorStatusEnum {
    RUN(1, "运行"),
    PAUSE(2, "暂停"),
    STOP(3, "停止")
    ;

    private Integer value;
    private String name;

    ExecutorStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
