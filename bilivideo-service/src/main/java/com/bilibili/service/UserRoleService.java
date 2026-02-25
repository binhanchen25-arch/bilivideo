package com.bilibili.service;

import com.bilibili.dao.UserRoleDao;
import com.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: 用户角色Service
 */
@Service
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    /**
     * 查询用户角色集合
     * @param userId 用户Id
     * @return
     */
    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);
    }

    /**
     * 添加用户的默认权限角色
     * @param userRole
     */
    public void addUserRole(UserRole userRole) {
        userRoleDao.addUserRole(userRole);
    }
}
