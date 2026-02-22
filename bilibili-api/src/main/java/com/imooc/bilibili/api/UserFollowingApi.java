package com.imooc.bilibili.api;

import com.bilibili.domain.FollowingGroup;
import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.UserFollowing;
import com.bilibili.service.UserFollowingService;
import com.imooc.bilibili.api.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: Dk.L
 * @date: 2023/3/22
 * @Description: 用户关注类
 */
@RestController
public class UserFollowingApi {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

    // 添加用户关注
    @PostMapping("/user-followings")
    public JsonResponse<String> addUserFollowings(@RequestBody UserFollowing userFolloing) {
        Long userId = userSupport.getCurrentUserId();
        userFolloing.setUserId(userId);
        userFollowingService.addUserFollowings(userFolloing);
        return JsonResponse.success();
    }

    // 获取用户关注列表（将关注用户按关注分组进行分类并返回）
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowings(){
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> result = userFollowingService.getUserFollowings(userId);
        return new JsonResponse<>(result);
    }

    // 获取用户的粉丝列表
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans(){
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowing> result = userFollowingService.getUserFans(userId);
        return new JsonResponse<>(result);
    }

    // 添加用户关注分组
    @PostMapping("/user-following-groups")
    public JsonResponse<Long> addUserFollowingGroup(@RequestBody FollowingGroup followingGroup){
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroup(followingGroup);
        return new JsonResponse<>(groupId);
    }

    // 获取用户分组
    @GetMapping("/user-following-group")
    public JsonResponse<List<FollowingGroup>> getUserFollowinggroups(){
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> list = userFollowingService.getUserFollowingGroups(userId);
        return new JsonResponse<>(list);
    }
}
