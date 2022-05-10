package com.kve.master.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The table 任务日志记录表
 */
@Data
public class OperateLogPageVO implements Serializable {

    private Integer total;

    private List<OperateLogItemVO> list;

    public static OperateLogPageVO initDefault() {
        OperateLogPageVO result = new OperateLogPageVO();
        result.setTotal(0);
        result.setList(new ArrayList<>(0));
        return result;
    }

    public OperateLogPageVO() {
    }

    public OperateLogPageVO(Integer total, List<OperateLogItemVO> list) {
        this.total = total;
        this.list = list;
    }


}
