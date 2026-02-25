package com.bilibili.service;

import com.bilibili.dao.UserCoinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: Dk.L
 * @date: 2023/3/30
 * @Description: 用户硬币Service
 */
@Service
public class UserCoinService {

    @Autowired
    private UserCoinDao userCoinDao;

    public Integer getUserCoinsAmount(Long userId) {
        return userCoinDao.getUserCoinsAmount(userId);
    }

    public void updateUserCoinsAmount(Long userId, Integer amount) {
        Date updateTime = new Date();
        userCoinDao.updateUserCoinsAmount(userId, amount, updateTime);
    }
}
