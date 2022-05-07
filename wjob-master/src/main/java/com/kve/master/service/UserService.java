package com.kve.master.service;

import com.kve.master.model.bean.UserInfo;
import com.kve.master.model.param.LoginParam;
import com.kve.master.model.param.UserPageParam;
import com.kve.master.model.param.UserParam;
import com.kve.master.model.param.UserUpdateParam;
import com.kve.master.model.vo.UserInfoDetailVO;
import com.kve.master.model.vo.UserInfoPageVO;

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
