package com.kve.master.mapper;

import com.kve.master.bean.TaskEntity;
import com.kve.master.bean.dto.TaskPageQueryDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * 根据ID查询任务
     */
    TaskEntity findById(@Param("id") Integer id);

    /**
     * 删除任务
     */
    void removeById(@Param("id") Integer id, @Param("updateBy") String updateBy, @Param("updateName") String updateName);

    /**
     * 根据条件分页查询任务
     */
    List<TaskEntity> listPageByCondition(TaskPageQueryDTO taskPageQueryDTO);

}
