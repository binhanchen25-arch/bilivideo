package com.bilibili.dao;

import com.bilibili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: Dk.L
 * @date: 2023/3/26
 * @Description: 角色Dao层
 */
@Mapper
public interface AuthRoleDao {

    AuthRole getRoleByCode(String code);
}
