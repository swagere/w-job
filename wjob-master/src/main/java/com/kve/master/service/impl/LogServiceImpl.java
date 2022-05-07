package com.kve.master.service.impl;

import cn.hutool.core.date.DateUtil;
import com.kve.master.bean.LogInfo;
import com.kve.master.bean.dto.LogPageQueryDTO;
import com.kve.master.bean.param.LogDetailParam;
import com.kve.master.bean.param.LogPageParam;
import com.kve.master.bean.vo.LogDetailVO;
import com.kve.master.bean.vo.LogItemVO;
import com.kve.master.bean.vo.LogPageVO;
import com.kve.master.mapper.LogInfoMapper;
import com.kve.master.service.LogService;
import com.kve.master.util.BeanCopyUtil;
import com.kve.master.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import sun.rmi.runtime.Log;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    LogInfoMapper logInfoMapper;

    /**
     * 分页日志列表
     *
     **/
    @Override
    public LogPageVO listPageLog(LogPageParam logPageParam) {
        //构建查询参数
        LogPageQueryDTO pageQueryDTO = buildLogQueryParam(logPageParam);

        List<LogItemVO> logList = logInfoMapper.listPageByCondition(pageQueryDTO);
        if (CollectionUtils.isEmpty(logList) || logList.size() <= 0) {
            return LogPageVO.initDefault();
        }
        return new LogPageVO(logList.size(), logList);
    }

    /**
     * 日志详情
     */
    @Override
    public LogDetailVO getLogDetail(LogDetailParam logDetailParam) {
        LogInfo logInfo = logInfoMapper.getById(logDetailParam.getId());
        return BeanCopyUtil.copy(logInfo, LogDetailVO.class);
    }

    /**
     * 构建查询参数
     */
    private LogPageQueryDTO buildLogQueryParam(LogPageParam logPageParam) {
        //构建查询参数
        return LogPageQueryDTO.builder()
                .limit(PageUtils.getStartRow(logPageParam.getPage(), logPageParam.getLimit()))
                .pageSize(PageUtils.getOffset(logPageParam.getLimit()))
                .logType(logPageParam.getLogType())
                .jobId(logPageParam.getJobId())
                .triggerNameLike(logPageParam.getTriggerNameLike())
                .operateId(logPageParam.getOperateId())
                .operateNameLike(logPageParam.getOperateNameLike())
                .contentLike(logPageParam.getContentLike())
                .createStartTime(StringUtils.isEmpty(logPageParam.getCreateStartTime()) ?
                        null : DateUtil.parseDateTime(logPageParam.getCreateStartTime()))
                .createEndTime(StringUtils.isEmpty(logPageParam.getCreateEndTime()) ?
                        null : DateUtil.parseDateTime(logPageParam.getCreateEndTime()))
                .build();
    }
}
