package com.kve.master.service.impl;

import cn.hutool.core.date.DateUtil;
import com.kve.master.model.bean.OperateLog;
import com.kve.master.model.dto.OperateLogPageQueryDTO;
import com.kve.master.model.param.OperateLogDetailParam;
import com.kve.master.model.param.OperateLogPageParam;
import com.kve.master.model.vo.OperateLogDetailVO;
import com.kve.master.model.vo.OperateLogItemVO;
import com.kve.master.model.vo.OperateLogPageVO;
import com.kve.master.mapper.OperateLogMapper;
import com.kve.master.service.OperateLogService;
import com.kve.master.util.BeanCopyUtil;
import com.kve.master.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    OperateLogMapper operateLogMapper;

    /**
     * 分页日志列表
     *
     **/
    @Override
    public OperateLogPageVO listPageLog(OperateLogPageParam operateLogPageParam) {
        //构建查询参数
        OperateLogPageQueryDTO pageQueryDTO = buildLogQueryParam(operateLogPageParam);

        List<OperateLogItemVO> logList = operateLogMapper.listPageByCondition(pageQueryDTO);
        if (CollectionUtils.isEmpty(logList) || logList.size() <= 0) {
            return OperateLogPageVO.initDefault();
        }
        return new OperateLogPageVO(logList.size(), logList);
    }

    /**
     * 日志详情
     */
    @Override
    public OperateLogDetailVO getLogDetail(OperateLogDetailParam operateLogDetailParam) {
        OperateLog operateLog = operateLogMapper.getById(operateLogDetailParam.getId());
        return BeanCopyUtil.copy(operateLog, OperateLogDetailVO.class);
    }

    /**
     * 构建查询参数
     */
    private OperateLogPageQueryDTO buildLogQueryParam(OperateLogPageParam operateLogPageParam) {
        //构建查询参数
        return OperateLogPageQueryDTO.builder()
                .limit(PageUtils.getStartRow(operateLogPageParam.getPage(), operateLogPageParam.getLimit()))
                .pageSize(PageUtils.getOffset(operateLogPageParam.getLimit()))
                .logType(operateLogPageParam.getLogType())
                .jobId(operateLogPageParam.getJobId())
                .triggerNameLike(operateLogPageParam.getTriggerNameLike())
                .operateId(operateLogPageParam.getOperateId())
                .operateNameLike(operateLogPageParam.getOperateNameLike())
                .contentLike(operateLogPageParam.getContentLike())
                .createStartTime(StringUtils.isEmpty(operateLogPageParam.getCreateStartTime()) ?
                        null : DateUtil.parseDateTime(operateLogPageParam.getCreateStartTime()))
                .createEndTime(StringUtils.isEmpty(operateLogPageParam.getCreateEndTime()) ?
                        null : DateUtil.parseDateTime(operateLogPageParam.getCreateEndTime()))
                .build();
    }
}
