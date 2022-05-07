package com.kve.master.mapper;

import com.kve.master.model.bean.UserInfo;
import com.kve.master.model.dto.UserPageQueryDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoMapper {
    UserInfo findByUserName(@Param("username") String username);

    /**
     * 根据ID更新
     */
    int updateById(UserInfo userInfo);

    /**
     * 根据条件分页查询任务
     *
     */
    List<UserInfo> listPageByCondition(UserPageQueryDTO pageQueryDTO);

    /**
     * 根据ID查询
     *
     */
    UserInfo findById(@Param("id") Integer id);
}
