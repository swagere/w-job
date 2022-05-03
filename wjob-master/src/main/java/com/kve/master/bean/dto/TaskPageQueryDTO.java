package com.kve.master.bean.dto;

import lombok.Builder;
import lombok.ToString;

import java.io.Serializable;

/**
 * 任务分页查询参数
 *
 * @author mengq
 */
@Builder
@ToString
public class TaskPageQueryDTO implements Serializable {

    private static final long serialVersionUID = -4537719275259204794L;

    private Integer limit;

    private Integer pageSize;

    private String triggerGroup;
    
    private String targetNameLike;

    private String targetMethodLike;

    private Integer jobStatus;

}
