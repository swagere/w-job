package com.kve.common.bean;

import com.kve.common.bean.base.BaseParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 任务相关 入参
 * @author: hujing39
 * @date: 2022-03-08
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskParam extends BaseParam {
    private Integer jobId;

    private String targetClass;

    private String targetMethod;

    private String triggerGroup;

    private String triggerName;

    private String description;

    private String cronExpression;

    private Integer jobStatus;

    private String targetArguments;

    private Map<String, Object> extendsMap;
}
