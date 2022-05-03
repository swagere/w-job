package com.kve.master.bean.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @author mengq
 * @version 1.0
 */
@Data
public class TaskPageParam implements Serializable {
    private static final long serialVersionUID = 3234394229041271191L;

    private Integer page;

    private Integer limit;

    private String targetNameLike;

    private String targetMethodLike;

    private Integer jobStatus;
}
