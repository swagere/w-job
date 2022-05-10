package com.kve.master.model.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScheduleLogPageParam implements Serializable {


    private static final long serialVersionUID = 8356494977556356252L;
    private Integer page;
    private Integer limit;

    private Integer triggerId;

}
