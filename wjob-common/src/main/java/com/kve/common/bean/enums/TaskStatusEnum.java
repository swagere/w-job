package com.kve.common.bean.enums;

/**
 * 任务状态：1 创建；2 运行；3 暂停； 4 恢复；5 已完成 停止 ；6 异常 停止
 */
public enum TaskStatusEnum {
    CREATE(1, "创建"),
    RUNNING(2, "运行"),
    PAUSE(3, "暂停"),
    RESUME(4, "恢复"),
    FINISH_SUCCESS(5, "已完成 停止"),
    FINISH_EXCEPTION(6, "异常 停止")
    ;

    private Integer value;
    private String name;

    TaskStatusEnum(Integer value, String name) {
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
