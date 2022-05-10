package com.kve.master.service;

import com.kve.master.model.param.OperateLogDetailParam;
import com.kve.master.model.param.OperateLogPageParam;
import com.kve.master.model.vo.OperateLogDetailVO;
import com.kve.master.model.vo.OperateLogPageVO;

public interface OperateLogService {
    OperateLogPageVO listPageLog(OperateLogPageParam operateLogPageParam);

    OperateLogDetailVO getLogDetail(OperateLogDetailParam operateLogDetailParam);
}
