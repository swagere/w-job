package com.kve.common.mapper;

import com.kve.common.bean.TaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author: hujing39
 * @date: 2022-03-15
 */

@Mapper
public interface TaskEntityMapper {
    /**
     * 更新任务
     */
    void updateByAppNameAndId(@Param("id") Integer id, @Param("appName") String appName);

    /**
     * 组和名称相同的任务
     */
    int countByJobDetail(@Param("appName") String appName,
                                     @Param("jobGroup") String jobGroup,
                                     @Param("jobClass") String jobClass,
                                     @Param("jobMethod") String jobMethod);

    /**
     * 新增task
     */
    void addTask(@Param("Task") TaskEntity task);

    /**
     * 根据项目与ID查询任务
     */
    TaskEntity findByAppNameAndId(@Param("id") Integer id, @Param("appName") String appName);

}
