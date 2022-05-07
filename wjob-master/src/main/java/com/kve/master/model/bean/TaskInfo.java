package com.kve.master.model.bean;

import com.kve.master.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 任务持久化
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskInfo extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Integer id;

    /**
     * 目标实例类
     */
    private String targetClass;

    /**
     * 目标实例方法
     */
    private String targetMethod;

    /**
     * 目标实例参数
     */
    private String targetArguments;

    /**
     * 任务描述
     */
    private String description;

    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 任务状态：1 创建；2 运行 3 暂停；4 恢复；5 已完成
     */
    private Integer jobStatus;

    /**
     * 调度器名-任务名称
     */
    private String triggerName;

    /**
     * 调度器分组-任务组
     */
    private String triggerGroup;


//    public String getTriggerName() {
//        return this.jobGroup + "." + this.jobName;
//    }

//    public String getQuartzJobName() {
//        return this.getJobClass() + "." + this.getJobMethod();
//    }
}
