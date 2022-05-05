package com.kve.master.service;

import com.kve.master.bean.UserInfo;
import com.kve.master.bean.param.LoginParam;
import com.kve.master.bean.param.UserPageParam;
import com.kve.master.bean.param.UserParam;
import com.kve.master.bean.param.UserUpdateParam;
import com.kve.master.bean.vo.UserInfoDetailVO;
import com.kve.master.bean.vo.UserInfoPageVO;

public interface UserService {
    UserInfo login(LoginParam loginParam) throws Exception;

    UserInfoDetailVO getUserPersonDetail(String username);

    UserInfoPageVO listPage(UserPageParam userPageParam, String username);

    UserInfoDetailVO getUserDetail(UserParam userParam);

    UserInfoDetailVO getUserDetailByUsername(String username);

    void updateUser(UserUpdateParam userUpdateParam, String username);

    void updatePwd(UserUpdateParam userUpdateParam, String username);

    void updateUserPower(UserUpdateParam userUpdateParam, String username);

    void deleteUser(UserUpdateParam userUpdateParam, String username);
}
