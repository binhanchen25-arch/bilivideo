package com.bilibili.service;

import com.bilibili.dao.AuthRoleDao;
import com.bilibili.domain.auth.AuthRole;
import com.bilibili.domain.auth.AuthRoleElementOperation;
import com.bilibili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: 用户角色Service
 */
@Service
public class AuthRoleService {

    @Autowired
    private AuthRoleDao authRoleDao;

    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    // 通过 roleIdSet 查询这些角色的所有操作权限
    public List<AuthRoleElementOperation> getRoleElementOperationsByUserIds(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getRoleElementOperationsByRoleIds(roleIdSet);
    }

    // 通过 roleIdSet 查询这些角色的所有菜单权限
    public List<AuthRoleMenu> getAuthRoleMenusByUserIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getAuthRoleMenusByUserIds(roleIdSet);
    }

    // 通过角色code查询角色信息
    public AuthRole getRoleByCode(String code) {
        return authRoleDao.getRoleByCode(code);
    }
}
