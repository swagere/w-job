package com.kve.master.model.dto;

import lombok.Builder;
import lombok.ToString;

import java.io.Serializable;

/**
 * 任务分页查询参数
 *
 */
@Builder
@ToString
public class TaskPageQueryDTO implements Serializable {

    private static final long serialVersionUID = -4537719275259204794L;

    private Integer limit;

    private Integer pageSize;

    private String triggerGroupLike;

    private String triggerNameLike;

    private String targetClassLike;

    private String targetMethodLike;

    private Integer jobStatus;

}
