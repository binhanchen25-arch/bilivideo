package com.bilibili.dao;

import com.alibaba.fastjson.JSONObject;
import com.bilibili.domain.RefreshTokenDetail;
import com.bilibili.domain.User;
import com.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author: Dk.L
 * @date: 2023/3/20
 * @Description: TODO
 */
@Mapper
public interface UserDao {

    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoById(Long id);

    void updateUsers(User user);

    void updateUserInfos(UserInfo userInfo);

    User getUserByPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);

    List<UserInfo> getUserInfoByUserIds(@Param("userIdList") Set<Long> userIdList);

    Integer pageCountUserInfos(Map<String, Object> params);

    List<UserInfo> pageListUserInfos(Map<String, Object> params);

    void deleteRefreshTokenByUserId(Long userId);

    void addRefreshToken(@Param("refreshToken") String refreshToken,
                         @Param("userId") Long userId,
                         @Param("createTime") Date creatTime);

    void deleteRefreshToken(@Param("refreshToken") String refreshToken,
                            @Param("userId") Long userId);

    RefreshTokenDetail getRefreshTokenDetail(String refreshToken);

    List<UserInfo> batchGetUserInfoByUserIds(@Param("userIdList") Set<Long> userIdList);
}
