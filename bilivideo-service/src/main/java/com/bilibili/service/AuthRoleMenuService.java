package com.bilibili.service;

import com.bilibili.dao.AuthRoleMenuDao;
import com.bilibili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: 角色权限Service
 */
@Service
public class AuthRoleMenuService {

    @Autowired
    private AuthRoleMenuDao authRoleMenuDao;

    /**
     * 通过 roleIdSet 查询这些角色的所有菜单权限
     *
     * @param roleIdSet 角色Id集
     * @return角色集所包含的菜单权限
     */
    public List<AuthRoleMenu> getAuthRoleMenusByUserIds(Set<Long> roleIdSet) {
        return authRoleMenuDao.getAuthRoleMenusByUserIds(roleIdSet);
    }
}
