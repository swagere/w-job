package com.kve.master.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户类型枚举
 */
public enum UserTypeEnum {

    /**
     * 超级管理员
     */
    SUPPER_ADMIN(1, 3),

    /**
     * 普通管理员
     */
    ADMIN(2, 2),

    /**
     * 普通用户
     */
    USER(3, 1),

    ;

    private Integer type;

    /**
     * 等级 越小越高
     */
    private Integer level;

    UserTypeEnum(Integer type, Integer level) {
        this.type = type;
        this.level = level;
    }

    public static Boolean isAdmin(Integer type) {
        if (SUPPER_ADMIN.getType().equals(type)) {
            return true;
        }
        return ADMIN.getType().equals(type);
    }

    public static Integer getLevelByType(Integer type) {
        return getEnumByType(type).getLevel();
    }

    public static UserTypeEnum getEnumByType(Integer type) {
        for (UserTypeEnum typeEnum : UserTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return USER;
    }

    public static Map<Integer, UserTypeEnum> getAllType() {
        Map<Integer, UserTypeEnum> enumMap = new HashMap<>(UserTypeEnum.values().length);
        for (UserTypeEnum typeEnum : UserTypeEnum.values()) {
            enumMap.put(typeEnum.getType(), typeEnum);
        }
        return enumMap;
    }

    public Integer getType() {
        return type;
    }

    public Integer getLevel() {
        return level;
    }
}
