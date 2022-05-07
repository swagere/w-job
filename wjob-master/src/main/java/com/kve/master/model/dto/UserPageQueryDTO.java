package com.kve.master.model.dto;

import lombok.Builder;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author mengq
 */
@Builder
@ToString
public class UserPageQueryDTO implements Serializable {

    private static final long serialVersionUID = 7120941057339818885L;
    
    private Integer limit;
    private Integer pageSize;

    private Integer userStatus;
    private Integer userType;
    private String usernameLike;

}
