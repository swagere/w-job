package com.kve.master.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.kve.master.bean.UserInfo;
import com.kve.master.bean.dto.UserPageQueryDTO;
import com.kve.master.bean.enums.DelFlagEnum;
import com.kve.master.bean.enums.UserStatusEnum;
import com.kve.master.bean.enums.UserTypeEnum;
import com.kve.master.bean.param.LoginParam;
import com.kve.master.bean.param.UserPageParam;
import com.kve.master.bean.param.UserParam;
import com.kve.master.bean.param.UserUpdateParam;
import com.kve.master.bean.vo.UserInfoDetailVO;
import com.kve.master.bean.vo.UserInfoPageDetailVO;
import com.kve.master.bean.vo.UserInfoPageVO;
import com.kve.master.config.exception.WJobException;
import com.kve.master.config.response.SysExceptionEnum;
import com.kve.master.mapper.UserInfoMapper;
import com.kve.master.service.UserService;
import com.kve.master.util.BeanCopyUtil;
import com.kve.master.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {


    @Autowired
    UserInfoMapper userInfoMapper;

    /**
     * 登录
     */
    public UserInfo login(LoginParam loginParam) throws Exception {
        UserInfo userInfo = userInfoMapper.findByUserName(loginParam.getUsername());
        if (userInfo == null || userInfo.getPassword() == null || !userInfo.getPassword().equals(loginParam.getPassword())) {
            throw new WJobException(SysExceptionEnum.USER_PASSWORD_ERROR);
        }
        updateLastLoginTime(userInfo);
        return userInfo;
    }
    /**
     * 个人详情
     *
     */
    @Override
    public UserInfoDetailVO getUserPersonDetail(String username) {
        UserInfo userInfo = userInfoMapper.findByUserName(username);
        if (userInfo == null || userInfo.getPassword() == null) {
            throw new WJobException(SysExceptionEnum.USER_NOT_EXIST);
        }
        UserInfoDetailVO userInfoDetailVO = BeanCopyUtil.copy(userInfo, UserInfoDetailVO.class);
        userInfoDetailVO.setUserStatusStr(UserStatusEnum.getByValue(userInfo.getUserStatus()).getName());
        userInfoDetailVO.setLastLoginTime("");
        if (userInfo.getLastRunTimestamp() > 0) {
            userInfoDetailVO.setLastLoginTime(DateUtil.formatDateTime(new Date(userInfo.getLastRunTimestamp())));
        }
        return userInfoDetailVO;
    }

    @Override
    public UserInfoPageVO listPage(UserPageParam userPageParam, String username) {
        //构建查询参数
        UserPageQueryDTO pageQueryDTO = UserPageQueryDTO.builder()
                .limit(PageUtils.getStartRow(userPageParam.getPage(), userPageParam.getLimit()))
                .pageSize(PageUtils.getOffset(userPageParam.getLimit()))
                .userStatus(userPageParam.getUserStatus())
                .userType(userPageParam.getUserType())
                .usernameLike(userPageParam.getUsernameLike())
                .build();

        List<UserInfo> userList = userInfoMapper.listPageByCondition(pageQueryDTO);
        if (CollectionUtils.isEmpty(userList) || userList.size() == 0) {
            return new UserInfoPageVO(userList.size(), new ArrayList<>(0));
        }

        return new UserInfoPageVO(userList.size(), this.buildPageResultList(userList, username));
    }

    @Override
    public UserInfoDetailVO getUserDetail(UserParam userParam) {
        UserInfo userInfo = userInfoMapper.findById(userParam.getId());
        if (userInfo == null) {
            throw new WJobException(SysExceptionEnum.USER_NOT_EXIST);
        }
        return BeanCopyUtil.copy(userInfo, UserInfoDetailVO.class);
    }

    @Override
    public UserInfoDetailVO getUserDetailByUsername(String username) {
        UserInfo userInfo = userInfoMapper.findByUserName(username);
        if (userInfo == null) {
            throw new WJobException(SysExceptionEnum.USER_NOT_EXIST);
        }
        return BeanCopyUtil.copy(userInfo, UserInfoDetailVO.class);
    }

    @Override
    public void updateUser(UserUpdateParam userUpdateParam, String username) {
        buildOperate(userUpdateParam, username);
        //校验用户是否存在
        UserInfo userInfo = userInfoMapper.findById(userUpdateParam.getId());
        if (null == userInfo) {
            throw new WJobException(SysExceptionEnum.USER_NOT_EXIST, userUpdateParam.getUsername());
        }
        //校验用户名否重复
        UserInfo count = userInfoMapper.findByUserName(userUpdateParam.getUsername());
        if (count != null && !count.getId().equals(userUpdateParam.getId())) {
            throw new WJobException(SysExceptionEnum.SAME_USER_NAME_EXISTS, userUpdateParam.getUsername());
        }
        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setId(userUpdateParam.getId());
        updateUserInfo.setUserStatus(userUpdateParam.getUserStatus());
        updateUserInfo.setUsername(userUpdateParam.getUsername());
        updateUserInfo.setUserType(userUpdateParam.getUserType());
        updateUserInfo.setUpdateBy(userUpdateParam.getOperateBy());
        updateUserInfo.setUpdateName(userUpdateParam.getOperateName());
        updateUserInfo.setRemarks(StringUtils.isEmpty(userUpdateParam.getRemarks()) ? "" : userUpdateParam.getRemarks());
        userInfoMapper.updateById(updateUserInfo);
        log.info("[ UserServiceImpl ] updateUser success saveParam:{}", JSON.toJSONString(userUpdateParam));

    }

    @Override
    public void updatePwd(UserUpdateParam userUpdateParam, String username) {
        buildOperate(userUpdateParam, username);
        //校验用户是否存在
        UserInfo userInfo = userInfoMapper.findById(userUpdateParam.getId());
        if (null == userInfo) {
            throw new WJobException(SysExceptionEnum.USER_NOT_EXIST, userUpdateParam.getUsername());
        }

        UserInfo userPwdInfo = new UserInfo();
        userPwdInfo.setId(userUpdateParam.getId());
        userPwdInfo.setPassword(userUpdateParam.getNewPassword());
        userPwdInfo.setUpdateBy(userUpdateParam.getOperateBy());
        userPwdInfo.setUpdateName(userUpdateParam.getOperateName());
        userInfoMapper.updateById(userPwdInfo);
        log.info("[ UserServiceImpl ] updatePwd success id:{},operateName:{}", userUpdateParam.getId(), userUpdateParam.getOperateName());
    }

    @Override
    public void updateUserPower(UserUpdateParam userUpdateParam, String username) {
        buildOperate(userUpdateParam, username);
        //校验用户是否存在
        UserInfo userInfo = userInfoMapper.findById(userUpdateParam.getId());
        if (null == userInfo) {
            throw new WJobException(SysExceptionEnum.USER_NOT_EXIST, userUpdateParam.getUsername());
        }

        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setId(userUpdateParam.getId());

        StringBuilder menusBuilder = new StringBuilder();
        if (!CollectionUtils.isEmpty(userUpdateParam.getMenus())) {
            for (String menu : userUpdateParam.getMenus()) {
                menusBuilder.append(",").append(menu);
            }
        }
        StringBuilder functionsBuilder = new StringBuilder();
        if (!CollectionUtils.isEmpty(userUpdateParam.getFunctions())) {
            for (String function : userUpdateParam.getFunctions()) {
                functionsBuilder.append(",").append(function);
            }
        }
        String menus = String.valueOf(menusBuilder);
        String functions = String.valueOf(functionsBuilder);
        updateUserInfo.setMenus(StringUtils.isEmpty(menus) ? "" : menus.substring(1));
        updateUserInfo.setFunctions(StringUtils.isEmpty(functions) ? "" : functions.substring(1));
        updateUserInfo.setUpdateBy(userUpdateParam.getOperateBy());
        updateUserInfo.setUpdateName(userUpdateParam.getOperateName());
        userInfoMapper.updateById(updateUserInfo);
        log.info("[ UserServiceImpl ] updateUserPower success saveParam:{}", JSON.toJSONString(userUpdateParam));

    }

    @Override
    public void deleteUser(UserUpdateParam userUpdateParam, String username) {
        buildOperate(userUpdateParam, username);

        //校验用户是否存在
        UserInfo userInfo = userInfoMapper.findById(userUpdateParam.getId());
        if (null == userInfo) {
            throw new WJobException(SysExceptionEnum.USER_NOT_EXIST, userUpdateParam.getUsername());
        }

        UserInfo userUpdateInfo = new UserInfo();
        userUpdateInfo.setId(userUpdateParam.getId());
        userUpdateInfo.setUpdateBy(userUpdateParam.getOperateBy());
        userUpdateInfo.setUpdateName(userUpdateParam.getOperateName());
        userUpdateInfo.setDelFlag(DelFlagEnum.DELETE.getValue());

        userInfoMapper.updateById(userUpdateInfo);
        log.info("[ UserServiceImpl ] deleteUser success id:{},operateName:{}", userUpdateParam.getId(), userUpdateParam.getOperateName());

    }

    private void buildOperate(UserUpdateParam userUpdateParam, String username) {
        UserInfo currentUser = userInfoMapper.findByUserName(username);
        if (null != currentUser) {
            userUpdateParam.setOperateBy(String.valueOf(currentUser.getId()));
            userUpdateParam.setOperateName(currentUser.getUsername());
        }
        else {
            throw new WJobException(SysExceptionEnum.USER_NOT_EXIST);
        }
    }

    /**
     * 构建分页结果集
     *
     */
    private List<UserInfoPageDetailVO> buildPageResultList(List<UserInfo> userList, String username) {
        UserInfoPageDetailVO detailVO = null;
        UserInfo currentUser = userInfoMapper.findByUserName(username);
        Map<Integer, UserTypeEnum> allType = UserTypeEnum.getAllType();
        List<UserInfoPageDetailVO> resultList = new ArrayList<>(userList.size());
        for (UserInfo userInfo : userList) {
            detailVO = BeanCopyUtil.copy(userInfo, UserInfoPageDetailVO.class);
            detailVO.setLastLoginTime("");
            if (userInfo.getLastRunTimestamp() > 0) {
                detailVO.setLastLoginTime(DateUtil.formatDateTime(new Date(userInfo.getLastRunTimestamp())));
            }
            if (allType.containsKey(userInfo.getUserType())) {
                detailVO.setUserTypeLevel(allType.get(userInfo.getUserType()).getLevel());
            }
            if (null != currentUser) {
                detailVO.setLoginUserTypeLevel(allType.get(currentUser.getUserType()).getLevel());
            }
            resultList.add(detailVO);
        }
        return resultList;
    }

    /**
     * 更新当前用户最后登录时间
     */
    private void updateLastLoginTime(UserInfo userInfo) {
        UserInfo updateUser = new UserInfo();
        updateUser.setId(userInfo.getId());
        updateUser.setLastRunTimestamp(System.currentTimeMillis());
        userInfoMapper.updateById(updateUser);
    }
}
