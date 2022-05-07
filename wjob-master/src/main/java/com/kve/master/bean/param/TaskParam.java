package com.kve.master.bean.param;

import com.kve.master.bean.base.BaseParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 任务相关 入参
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskParam extends BaseParam {
    private Integer id;

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
