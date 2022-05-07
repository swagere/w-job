package com.kve.master.bean.enums;

public enum DelFlagEnum {

    /**
     * 正常
     */
    NORMAL(0),

    /**
     * 删除
     */
    DELETE(1);

    private Integer value;

    DelFlagEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
