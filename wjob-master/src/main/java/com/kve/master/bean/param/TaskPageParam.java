package com.kve.master.bean.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskPageParam implements Serializable {
    private static final long serialVersionUID = 3234394229041271191L;

    private Integer page;

    private String triggerGroupLike;

    private Integer limit;

    private String triggerNameLike;

    private String targetClassLike;

    private String targetMethodLike;

    private Integer jobStatus;
}
