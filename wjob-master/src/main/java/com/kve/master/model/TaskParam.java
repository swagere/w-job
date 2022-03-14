package com.kve.master.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: hujing39
 * @date: 2022-03-08
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskParam {
    private String jobGroup;
    private String jobName;
    private String jobClassName;
    private String description;

    private String concurrentDegree;
    private String triggerType;

    private Integer rate;
    private Integer times;

    private String cronExpression;
    private String second;
    private String minute;
    private String hour;
    private String day;
    private String mouth;
    private String week;
}
