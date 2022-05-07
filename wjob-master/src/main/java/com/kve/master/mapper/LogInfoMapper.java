package com.kve.master.mapper;

import com.kve.master.bean.LogInfo;
import com.kve.master.bean.dto.LogPageQueryDTO;
import com.kve.master.bean.vo.LogItemVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务日志记录表
 *
 */
@Repository
public interface LogInfoMapper {

    /**
     * 新增日志
     */
    void addLog(LogInfo entity);

    /**
     * 根据日志ID查询
     */
    LogInfo getById(Long id);

    /**
     * 根据任务ID查询
     */
    List<LogInfo> getByJobId(Integer jobId);


    /**
     * 根据条件分页查询任务
     */
    List<LogItemVO> listPageByCondition(LogPageQueryDTO logPageQueryDTO);

}
