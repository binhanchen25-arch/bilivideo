package com.bilibili.dao;

import com.bilibili.domain.auth.UserRole;

import java.util.List;

/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: TODO
 */
public interface UserRoleDao {

    List<UserRole> getUserRoleByUserId(Long userId);

    void addUserRole(UserRole userRole);
}
