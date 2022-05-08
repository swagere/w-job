package com.kve.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel implements Serializable {
    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";

    private String status;
    private String msg;

    @Override
    public String toString() {
        return "ResponseModel{" +
                "status='" + status +
                ", msg='" + msg +
                '}';
    }

}
