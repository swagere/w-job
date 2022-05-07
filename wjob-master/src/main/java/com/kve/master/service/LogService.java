package com.kve.master.service;

import com.kve.master.bean.param.LogDetailParam;
import com.kve.master.bean.param.LogPageParam;
import com.kve.master.bean.vo.LogDetailVO;
import com.kve.master.bean.vo.LogPageVO;

public interface LogService {
    LogPageVO listPageLog(LogPageParam logPageParam);

    LogDetailVO getLogDetail(LogDetailParam logDetailParam);
}
