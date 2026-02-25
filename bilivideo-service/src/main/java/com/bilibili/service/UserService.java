package com.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.bilibili.dao.UserDao;
import com.bilibili.domain.PageResult;
import com.bilibili.domain.RefreshTokenDetail;
import com.bilibili.domain.User;
import com.bilibili.domain.UserInfo;
import com.bilibili.domain.constant.UserConstant;
import com.bilibili.domain.exception.ConditionException;
import com.bilibili.service.util.MD5Util;
import com.bilibili.service.util.RSAUtil;
import com.bilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author: Dk.L
 * @date: 2023/3/20
 * @Description: TODO
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthService userAuthService;

    /**
     * 通过手机号获得用户
     * @param phone 手机号
     * @return 查询得到的用户
     */
    public User getUserByPhone(String phone){
        User user = userDao.getUserByPhone(phone);
        return user;
    }

    /**
     * 添加新用户
     * @param user 注册信息实体
     * @return
     */
    public void addUser(User user){
        String  phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空白！");
        }
        User dbUser = userDao.getUserByPhone(user.getPhone());
        if(dbUser != null){
            throw new ConditionException("手机号已注册！");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("解密失败！");
        }
        String md5Password = MD5Util.sign(rawPassword, salt, "utf-8");
        user.setSalt(salt);
        user.setCreateTime(now);
        user.setPassword(md5Password);
        userDao.addUser(user);
        // 添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_FEMALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
        // 添加新用户权限
        userAuthService.addUserDefaultRole(user.getId());
    }

    /**
     * 用户登录
     * @param user 用户信息
     * @return
     */
    public String login(User user) throws Exception{
        String phone = user.getPhone();
        String email = user.getEmail();
        if (StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)) {
            throw new ConditionException("参数异常，手机号和邮箱不能全为空！");
        }
        User dbUser = userDao.getUserByPhoneOrEmail(phone, email);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在! ");
        }
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败! ");
        }
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if(!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误! ");
        }
        return TokenUtil.generateToken(dbUser.getId());
    }

    /**
     * 根据userId查询用户信息
     * @param userId 用户Id
     * @return 用户
     */
    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoById(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    /**
     * 更新用户
     * @param user user实体
     * @throws Exception
     */
    public void updateUsers(User user) throws Exception{
        Long id = user.getId();
        User dbUser = userDao.getUserById(id);
        if (id == null) {
            throw new ConditionException("用户不存在");
        }
        if (!StringUtils.isNullOrEmpty(user.getPassword())) {
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "utf-8");
            user.setPassword(md5Password);
        }
        user.setUpdateTime(new Date());
        userDao.updateUsers(user);
    }

    /**
     * 更新用户信息
     * @param userInfo 用户信息
     */
    public void updateUserInfos (UserInfo userInfo){
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfos(userInfo);
    }

    /**
     * 通过用户所关注用户的Id查询被关注者信息
     * @param followingId
     * @return
     */
    public User getUserById(Long followingId){
        return userDao.getUserById(followingId);
    }

    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList){
        return userDao.getUserInfoByUserIds(userIdList);
    }

    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = params.getInteger("no");
        Integer size = params.getInteger("size");
        // 查询数据库的参数
        params.put("start", (no-1)*size);
        params.put("limit", size);
        // 通过nick 进行模糊条件查询用户数量
        Integer total = userDao.pageCountUserInfos(params);
        List<UserInfo> list = new ArrayList();
        if(total > 0){
            list = userDao.pageListUserInfos(params);
        }
        return new PageResult<>(total, list);
    }

    /**
     * 登录返回双Token
     * @param user
     * @return
     */
    public Map<String, Object> loginForDts(User user) throws  Exception{
        // 手机号或邮箱登录
        String email = user.getEmail() == null ? "" : user.getEmail();
        String phone = user.getPhone() == null ? "" : user.getPhone();
        if (StringUtils.isNullOrEmpty(email) && StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("参数异常，手机号和邮箱不能全为空");
        }
        User dbUser = userDao.getUserByPhoneOrEmail(phone, email);
        if(dbUser == null){
            throw new ConditionException("用户不存在");
        }
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if (!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误");
        }
        Long userId = dbUser.getId();
        // 得到双token
        String accessToken = TokenUtil.generateToken(userId);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        // 保存 refresh-token到数据库
        userDao.deleteRefreshTokenByUserId(userId);
        userDao.addRefreshToken(refreshToken, userId, new Date());
        Map<String, Object> result = new HashMap<>();
        result.put("refreshToken", refreshToken);
        result.put("accessToken", accessToken);
        return result;
    }

    /**
     * 删除刷新令牌
     * @param refreshToken 刷新令牌
     * @param userId 用户Id
     */
    public void logout(String refreshToken, Long userId){
        userDao.deleteRefreshToken(refreshToken,userId);
    }

    public String refreshAccessToken(String refreshToken) throws Exception{
        RefreshTokenDetail refreshTokenDetail = userDao.getRefreshTokenDetail(refreshToken);
        if(refreshTokenDetail == null){
            throw new ConditionException("555", "refreshAccessToken过期");
        }
        Long userId = refreshTokenDetail.getUserId();
        return TokenUtil.generateToken(userId);
    }

    public List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.batchGetUserInfoByUserIds(userIdList);
    }
}
