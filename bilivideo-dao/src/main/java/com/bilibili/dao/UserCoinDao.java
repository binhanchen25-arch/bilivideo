package com.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author: Dk.L
 * @date: 2023/3/30
 * @Description: 用户的硬币类
 */
@Mapper
public interface UserCoinDao {

    Integer getUserCoinsAmount(Long userId);

    Integer updateUserCoinsAmount(@Param("userId") Long userId,
                                  @Param("amount") Integer amount,
                                  @Param("updateTime") Date updateTime);
}
