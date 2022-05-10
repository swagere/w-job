package com.kve.master.model.vo;

import com.kve.master.model.bean.ScheduleLog;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The table 任务日志记录表
 */
@Data
public class ScheduleLogPageVO implements Serializable {

    private Integer total;

    private List<ScheduleLog> list;

    public static ScheduleLogPageVO initDefault() {
        ScheduleLogPageVO result = new ScheduleLogPageVO();
        result.setTotal(0);
        result.setList(new ArrayList<>(0));
        return result;
    }

    public ScheduleLogPageVO() {
    }

    public ScheduleLogPageVO(Integer total, List<ScheduleLog> list) {
        this.total = total;
        this.list = list;
    }


}
