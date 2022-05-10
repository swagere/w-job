package com.kve.master.mapper;

import com.kve.master.model.bean.ScheduleLog;
import com.kve.master.model.dto.ScheduleLogPageQueryDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务日志记录表
 *
 */
@Repository
public interface ScheduleLogMapper {
    void save(ScheduleLog scheduleLog);

    void updateById(ScheduleLog scheduleLog);

    ScheduleLog findById(Integer id);

    void deleteByTriggerId(Integer id);

    /**
     * 根据条件分页查询任务
     */
    List<ScheduleLog> listPageByCondition(ScheduleLogPageQueryDTO scheduleLogPageQueryDTO);


}
