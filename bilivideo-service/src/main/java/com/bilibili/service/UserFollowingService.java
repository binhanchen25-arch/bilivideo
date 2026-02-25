package com.bilibili.service;

import com.bilibili.dao.UserFollowingDao;
import com.bilibili.domain.FollowingGroup;
import com.bilibili.domain.User;
import com.bilibili.domain.UserFollowing;
import com.bilibili.domain.UserInfo;
import com.bilibili.domain.constant.UserConstant;
import com.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: Dk.L
 * @date: 2023/3/22
 * @Description: TODO
 */
@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    /**
     * 添加用户关注
     * @param userFollowing 用户关注信息
     */
    @Transactional
    public void addUserFollowings(UserFollowing userFollowing){
        Long groupId = userFollowing.getGroupId();
        if(groupId == null){
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        }else{
            FollowingGroup followingGroup = followingGroupService.getById(userFollowing.getGroupId());
            if(followingGroup == null){
                throw new ConditionException("关注分组不存在！");
            }
        }
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);
        if(user == null){
            throw new ConditionException("关注的用户不存在！");
        }
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }

    /**
     * 获取用户关注列表(按关注分组进行分类返回)
     * @param userId 用户Id
     * @return 用户关注列表
     */
    // 第一步：获取关注的用户列表
    // 第二步：根据关注用户的id查询关注用户的基本信息
    // 第三步：将关注用户按照关注分组进行分类
    public List<FollowingGroup> getUserFollowings(Long userId){
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        if(followingIdSet.size() > 0){
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        // 查询关注用户的基本信息
        for(UserFollowing userFollowing : list){
            for(UserInfo userInfo : userInfoList){
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        FollowingGroup allGroup = new FollowingGroup();
        // 全部关注列表
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        // 将关注用户按照关注分组进行分类
        for (FollowingGroup followingGroup : groupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for(UserFollowing userFollowing : list){
                if (followingGroup.getId().equals(userFollowing.getGroupId())) {
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            followingGroup.setFollowingUserInfoList(infoList);
            result.add(followingGroup);
        }
        return result;
    }


    // 第一步：获取当前用户的粉丝列表
    // 第二步：根据粉丝的用户Id查询基本信息
    // 第三步：查询当前用户是否关注该粉丝
    public List<UserFollowing> getUserFans(Long userId){
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        if(fanIdSet.size() > 0){
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        for (UserFollowing fan : fanList) {
            // 查询粉丝基本信息
            for (UserInfo userInfo : userInfoList) {
                if (fan.getUserId().equals(userInfo.getUserId())) {
                    // 先对互相关注的初始化状态设置为false
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
            // 查询是否相互关注
            for (UserFollowing following : followingList){
                if (following.getFollowingId().equals(fan.getUserId())){
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return fanList;
    }

    /**
     * 添加关注分组
     *
     * @param followingGroup 分组信息
     * @return 添加分组Id
     */
    public Long addUserFollowingGroup(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addUserFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    /**
     * 获取关注分组
     * @param userId 用户ID
     * @return 分组信息
     */
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        List<FollowingGroup> list = followingGroupService.getUserFollowingGroups(userId);
        return list;
    }

    public List<UserInfo> checkUserInfoList(List<UserInfo> userInfoList, Long userId) {
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        for (UserInfo userInfo : userInfoList) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : userFollowingList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}
