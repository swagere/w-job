package com.kve.master.model.enums;

public enum UserStatusEnum {

    /**
     * 默认值
     */
    DEFAULT(-1, ""),

    /**
     * 状态 启用
     */
    ENABLE(1, "启用"),

    /**
     * 停用
     */
    DISABLE(2, "停用"),

    /**
     * 锁定
     */
    LOCK(3, "锁定");


    private Integer value;
    private String name;

    UserStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;


    }

    public static UserStatusEnum getByValue(Integer value) {
        for (UserStatusEnum statusEnum : UserStatusEnum.values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }
        return UserStatusEnum.DEFAULT;
    }

}
