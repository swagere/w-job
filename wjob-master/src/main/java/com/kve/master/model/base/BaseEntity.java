package com.kve.master.model.base;

import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {
    /**
     * 最后一次运行的时间
     */
    private Long LastRunTimestamp;

    /**
     * 创建人ID
     */
    private String createBy;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最新修改人
     */
    private String updateBy;

    /**
     * 最新修改人名称
     */
    private String updateName;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标记
     */
    private Integer delFlag;
}