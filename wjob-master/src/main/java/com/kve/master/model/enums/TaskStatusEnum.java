package com.kve.master.model.enums;

/**
 * 任务状态：1 创建；2 运行；3 暂停；4 已完成 停止 ；5 异常 停止
 */
public enum TaskStatusEnum {
    CREATE(1, "创建"),
    RUNNING(2, "运行"),
    PAUSE(3, "暂停"),
    FINISH_SUCCESS(4, "任务执行完成"),
    FINISH_EXCEPTION(5, "中断/异常停止")
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
