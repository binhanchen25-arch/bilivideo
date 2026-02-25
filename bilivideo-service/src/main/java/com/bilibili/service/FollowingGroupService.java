package com.bilibili.service;

import com.bilibili.dao.FollowingGroupDao;
import com.bilibili.domain.FollowingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Dk.L
 * @date: 2023/3/22
 * @Description: TODO
 */
@Service
public class FollowingGroupService {

    @Autowired
    FollowingGroupDao followingGroupDao;

    /**
     * 通过类型查找关注组
     * @param type
     * @return
     */
    public FollowingGroup getByType(String type) {
        return followingGroupDao.getByType(type);
    }

    /**
     * 通过Id查找关注组
     * @param id
     * @return
     */
    public FollowingGroup getById(Long id) {
        return followingGroupDao.getById(id);
    }

    /**
     * 通过用户Id查询关注分组
     * @param userId
     * @return
     */
    public List<FollowingGroup> getByUserId(Long userId) {
        return followingGroupDao.getByUserId(userId);
    }

    /**
     * 添加用户分组
     * @param followingGroup
     */
    public void addUserFollowingGroup(FollowingGroup followingGroup) {
        followingGroupDao.addUserFollowingGroup(followingGroup);
    }

    /**
     * 获取关注分组
     * @param userId
     * @return
     */
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        List<FollowingGroup> list = followingGroupDao.getUserFolloingGroups(userId);
        return list;
    }
}

