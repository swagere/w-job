package com.kve.master.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 任务日志查询参数
 *
 */
@Builder
@ToString
@Data
public class ScheduleLogPageQueryDTO implements Serializable {

    private static final long serialVersionUID = -2794336177217543563L;

    private Integer limit;
    private Integer pageSize;

    private Integer triggerId;
}
