package com.kve.master.bean.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @author mengq
 * @version 1.0
 */
@Data
public class UserPageParam implements Serializable {

    private static final long serialVersionUID = 4443152381870746507L;

    private Integer page;
    private Integer limit;

    private Integer userStatus;
    private Integer userType;

    private String usernameLike;

}
