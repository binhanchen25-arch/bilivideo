package com.imooc.bilibili.api.aspect;

import com.bilibili.domain.annotation.ApiLimitedRole;
import com.bilibili.domain.auth.UserRole;
import com.bilibili.domain.exception.ConditionException;
import com.bilibili.service.UserRoleService;
import com.imooc.bilibili.api.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: Dk.L
 * @date: 2023/3/25
 * @Description: TODO
 */
@Aspect
@Component
@Order(1)
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.bilibili.domain.annotation.ApiLimitedRole)")
    public void check(){
    }

    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefor(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole){
        Long userId = userSupport.getCurrentUserId();
        // 查询用户角色集合
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        // 获取注解参数列表 limitedRoleCodeSet(需要拦截的角色)
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        // 获取用户角色代码
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        // 二者取交集
        roleCodeSet.retainAll(limitedRoleCodeSet);
        if (roleCodeSet.size() > 0){
            throw new ConditionException("权限不足！无法调用相应接口");
        }
    }
}