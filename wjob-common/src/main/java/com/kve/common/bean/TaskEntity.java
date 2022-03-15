package com.kve.common.bean;

import com.kve.common.bean.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 任务持久化
 * @author: hujing39
 * @date: 2022-03-14
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Integer id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 任务接口类名
     */
    private String jobClass;

    /**
     * 任务接口方法
     */
    private String jobMethod;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务描述
     */
    private String description;

    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 任务状态：1 未开始；2 暂停；3 恢复；4 已完成
     */
    private Integer jobStatus;

    /**
     * 任务参数
     */
    private String JobArguments;

    public String getTriggerName() {
        return "job" + ":" + this.getJobClass() + ":" + this.getJobMethod() + ":Trigger";
    }

    public String getQuartzJobName() {
        return this.getJobClass() + "." + this.getJobMethod();
    }
}
