package com.kve.master.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The table 任务日志记录表
 */
@Data
public class LogPageVO implements Serializable {

    private Integer total;

    private List<LogItemVO> list;

    public static LogPageVO initDefault() {
        LogPageVO result = new LogPageVO();
        result.setTotal(0);
        result.setList(new ArrayList<>(0));
        return result;
    }

    public LogPageVO() {
    }

    public LogPageVO(Integer total, List<LogItemVO> list) {
        this.total = total;
        this.list = list;
    }


}
