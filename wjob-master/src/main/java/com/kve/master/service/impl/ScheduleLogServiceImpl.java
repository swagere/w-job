package com.kve.master.service.impl;

import com.kve.master.config.exception.WJobException;
import com.kve.master.config.response.SysExceptionEnum;
import com.kve.master.mapper.ScheduleLogMapper;
import com.kve.master.model.bean.ScheduleLog;
import com.kve.master.model.dto.ScheduleLogPageQueryDTO;
import com.kve.master.model.param.ScheduleLogPageParam;
import com.kve.master.model.vo.ScheduleLogPageVO;
import com.kve.master.service.ScheduleLogService;
import com.kve.master.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ScheduleLogServiceImpl implements ScheduleLogService {

    @Autowired
    ScheduleLogMapper scheduleLogMapper;

    /**
     * 分页日志列表
     *
     **/
    @Override
    public ScheduleLogPageVO listPageLog(ScheduleLogPageParam scheduleLogPageParam) {
        //构建查询参数
        ScheduleLogPageQueryDTO pageQueryDTO = buildLogQueryParam(scheduleLogPageParam);

        if (pageQueryDTO.getTriggerId() == null) {
            throw new WJobException(SysExceptionEnum.INVALID_PARAM);
        }

        List<ScheduleLog> logList = scheduleLogMapper.listPageByCondition(pageQueryDTO);
        if (CollectionUtils.isEmpty(logList) || logList.size() <= 0) {
            return ScheduleLogPageVO.initDefault();
        }
        return new ScheduleLogPageVO(logList.size(), logList);
    }

    /**
     * 构建查询参数
     */
    private ScheduleLogPageQueryDTO buildLogQueryParam(ScheduleLogPageParam scheduleLogPageParam) {
        //构建查询参数
        return ScheduleLogPageQueryDTO.builder()
                .limit(PageUtils.getStartRow(scheduleLogPageParam.getPage(), scheduleLogPageParam.getLimit()))
                .pageSize(PageUtils.getOffset(scheduleLogPageParam.getLimit()))
                .triggerId(scheduleLogPageParam.getTriggerId())
                .build();
    }
}
