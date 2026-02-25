package com.imooc.bilibili.api.support;

import com.bilibili.domain.exception.ConditionException;
import com.bilibili.service.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;

/**
 * @author: Dk.L
 * @date: 2023/3/20
 * @Description: 用户支撑功能类
 */
@Component
public class UserSupport {
    public Long getCurrentUserId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = requestAttributes.getRequest().getHeader("token");
        Long userId = TokenUtil.verifyToken(token);
        if(userId < 0){
            throw new ConditionException("非法用户");
        }
        return userId;
    }
}
