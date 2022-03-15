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

    private String jobClass;

    private String jobMethod;

    private String jobGroup;

    private String jobName;

    private String description;

    private String cronExpression;

    /**
     * 任务状态：1 创建；2 暂停；3 恢复；4 已完成
     */
    private Integer jobStatus;

    private String methodArgs;

    private Map<String, Object> extendsMap;
}
