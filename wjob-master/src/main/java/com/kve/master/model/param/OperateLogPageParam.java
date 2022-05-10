package com.kve.master.model.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class OperateLogPageParam implements Serializable {


    private static final long serialVersionUID = 8356494977556356252L;
    private Integer page;
    private Integer limit;

    private Integer logType;
    private Integer jobId;
    private String triggerNameLike;


    private String operateId;
    private String operateNameLike;
    private String contentLike;

    private String createStartTime;
    private String createEndTime;

}
