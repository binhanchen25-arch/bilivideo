package com.bilibili.dao;

import com.bilibili.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: 角色拥有菜单Dao
 */
public interface AuthRoleMenuDao {
    List<AuthRoleMenu> getAuthRoleMenusByUserIds(@Param("roleIdSet") Set<Long> roleIdSet);
}
