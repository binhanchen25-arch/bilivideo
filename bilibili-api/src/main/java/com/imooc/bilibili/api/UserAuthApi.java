package com.imooc.bilibili.api;

import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.auth.UserAuthorities;
import com.bilibili.service.UserAuthService;
import com.imooc.bilibili.api.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: 用户权限
 */
@RestController
public class UserAuthApi {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserAuthService userAuthService;

    // 获得用户所拥有的权限详细信息（操作权限、菜单权限）
    @GetMapping("/user-authorities")
    public JsonResponse<UserAuthorities> getUserAuthorities(){
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        return new JsonResponse<>(userAuthorities);
    }

}
