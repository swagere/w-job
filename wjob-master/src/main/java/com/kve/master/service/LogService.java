package com.kve.master.service;

import com.kve.master.model.param.LogDetailParam;
import com.kve.master.model.param.LogPageParam;
import com.kve.master.model.vo.LogDetailVO;
import com.kve.master.model.vo.LogPageVO;

public interface LogService {
    LogPageVO listPageLog(LogPageParam logPageParam);

    LogDetailVO getLogDetail(LogDetailParam logDetailParam);
}
