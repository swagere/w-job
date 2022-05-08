package com.kve.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestModel implements Serializable {

    private long timestamp;
    private String action;

    private Integer triggerId;
    private String targetClass;
    private String targetMethod;
    private String targetArguments;

    private String status;
    private String msg;
}
