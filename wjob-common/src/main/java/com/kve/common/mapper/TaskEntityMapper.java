package com.kve.common.mapper;

import com.kve.common.bean.TaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author: hujing39
 * @date: 2022-03-15
 */

@Repository
public interface TaskEntityMapper {
    /**
     * 更新任务
     */
    void updateById(TaskEntity task);

    /**
     * trigger组和名称相同的任务
     */
    int countByTriggerDetail(@Param("triggerGroup") String triggerGroup, @Param("triggerName") String triggerName);

    /**
     * 新增task
     */
    void addTask(TaskEntity task);

    /**
     * 根据项目与ID查询任务
     */
    TaskEntity findById(@Param("id") Integer id);

}
