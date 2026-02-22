package com.bilibili.service;

import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.auth.*;
import com.bilibili.domain.constant.AuthRoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: 用户权限Service层
 */
@Service
public class UserAuthService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthRoleService authRoleService;

    public UserAuthorities getUserAuthorities(Long userId) {
        // 查询用户角色Id集合 roleIdSet
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<Long> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        // 查询角色的按钮操作权限
        List<AuthRoleElementOperation> roleElementOperationList = authRoleService.getRoleElementOperationsByUserIds(roleIdSet);
        // 查询页面菜单权限
        List<AuthRoleMenu> authRoleMenuList = authRoleService.getAuthRoleMenusByUserIds(roleIdSet);
        // 封装用户的所有权限
        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(roleElementOperationList);
        userAuthorities.setRoleMenuList(authRoleMenuList);
        return userAuthorities;
    }

    /**
     * 添加新用户的默认权限角色
     * @param id 用户Id
     */
    public void addUserDefaultRole(Long id) {
        // 新建(用户)->(角色)实体类
        UserRole userRole = new UserRole();
        // 通过默认角色code查询，默认角色的信息Id
        AuthRole role = authRoleService.getRoleByCode(AuthRoleConstant.ROLE_LV0);
        // 添加用户的默认权限角色
        userRole.setUserId(id);
        userRole.setRoleId(role.getId());
        userRole.setCreateTime(new Date());
        userRoleService.addUserRole(userRole);
    }
}
