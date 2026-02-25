package com.bilibili.dao;

import com.bilibili.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.Mapping;

/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: TODO
 */
@Mapper
public interface UserMomentsDao {
    Integer addUserMoments(UserMoment userMoment);
}
