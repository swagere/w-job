package com.kve.master.model.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleLog implements Serializable {
    private Integer id;

    private Integer triggerId;

    private String executorAddress;	// 执行器地址，有多个则逗号分隔

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date triggerTime;
    private String triggerStatus;
    private String triggerMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date executeTime;
    private String executeStatus;
    private String executeMsg;
}
