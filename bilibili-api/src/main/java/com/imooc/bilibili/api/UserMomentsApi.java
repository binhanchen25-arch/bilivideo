package com.imooc.bilibili.api;

import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.UserMoment;
import com.bilibili.domain.annotation.ApiLimitedRole;
import com.bilibili.domain.annotation.DataLimited;
import com.bilibili.domain.constant.AuthRoleConstant;
import com.bilibili.service.UserMomentsService;
import com.imooc.bilibili.api.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: TODO
 */
@RestController
public class UserMomentsApi {

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;

    // 添加新动态, 接口权限控制
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})
    // 数据权限控制
    @DataLimited
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }

    // 查询该用户关注的人的动态信息
    @GetMapping("/user-subscribed-moment")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments(){
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list = userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }
}
