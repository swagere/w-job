package com.kve.master.bean.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TaskDetailVO implements Serializable {


    private static final long serialVersionUID = 4406595135819210411L;

    /**
     * 任务ID
     */
    private Integer id;

    /**
     * 任务接口类名
     */
    private String targetClass;

    /**
     * 任务接口方法
     */
    private String targetMethod;

    /**
     * 任务参数
     */
    private String targetArguments;

    /**
     * 任务分组
     */
    private String triggerGroup;

    /**
     * 任务名称
     */
    private String triggerName;

    /**
     * 任务状态
     */
    private Integer jobStatus;

    /**
     * 任务时间表达式
     */
    private String cronExpression;

    /**
     * 任务描述
     */
    private String description;

    private String LastRunTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;
    /**
     * 最新修改人
     */
    private String updateBy;


}
