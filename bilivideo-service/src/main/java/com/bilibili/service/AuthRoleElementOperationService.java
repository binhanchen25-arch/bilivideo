package com.bilibili.service;

import com.bilibili.dao.AuthRoleElementOperationDao;
import com.bilibili.domain.auth.AuthRoleElementOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author: Dk.L
 * @date: 2023/3/24
 * @Description: 角色元素操作权限
 */
@Service
public class AuthRoleElementOperationService {

    @Autowired
    private AuthRoleElementOperationDao authRoleElementOperationDao;

    /**
     * 通过角色集查询这些角色的所有操作权限
     *
     * @param roleIdSet 角色Id集
     * @return
     */
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationDao.getRoleElementOperationsByRoleIds(roleIdSet);
    }
}
