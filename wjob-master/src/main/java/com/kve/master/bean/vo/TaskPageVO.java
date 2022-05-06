package com.kve.master.bean.vo;


import com.kve.master.bean.TaskInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskPageVO implements Serializable {

    private static final long serialVersionUID = 8763039540705743762L;

    private Integer total;

    private List<TaskDetailVO> list;

    public static TaskPageVO initDefault() {
        TaskPageVO result = new TaskPageVO();
        result.setTotal(0);
        result.setList(new ArrayList<>(0));
        return result;
    }

    public TaskPageVO() {
    }

    public TaskPageVO(Integer total, List<TaskDetailVO> list) {
        this.total = total;
        this.list = list;
    }

}
