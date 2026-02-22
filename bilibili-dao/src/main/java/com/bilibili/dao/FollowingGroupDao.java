package com.bilibili.dao;

import com.bilibili.domain.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: Dk.L
 * @date: 2023/3/22
 * @Description: TODO
 */
@Mapper
public interface FollowingGroupDao {

    FollowingGroup getByType(String type);

    FollowingGroup getById(Long id);

    List<FollowingGroup> getByUserId(Long userId);

    Integer addUserFollowingGroup(FollowingGroup followingGroup);

    List<FollowingGroup> getUserFolloingGroups(Long userId);
}
