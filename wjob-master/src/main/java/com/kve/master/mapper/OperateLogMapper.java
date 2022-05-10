package com.kve.master.mapper;

import com.kve.master.model.bean.OperateLog;
import com.kve.master.model.dto.OperateLogPageQueryDTO;
import com.kve.master.model.vo.OperateLogItemVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务日志记录表
 *
 */
@Repository
public interface OperateLogMapper {

    /**
     * 新增日志
     */
    void addLog(OperateLog entity);

    /**
     * 根据日志ID查询
     */
    OperateLog getById(Long id);

    /**
     * 根据任务ID查询
     */
    List<OperateLog> getByJobId(Integer jobId);


    /**
     * 根据条件分页查询任务
     */
    List<OperateLogItemVO> listPageByCondition(OperateLogPageQueryDTO operateLogPageQueryDTO);

}
