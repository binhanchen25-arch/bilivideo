package com.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bilibili.dao.DanmuDao;
import com.bilibili.domain.Danmu;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: Dk.L
 * @date: 2023/3/31
 * @Description: 弹幕服务层
 */
@Service
public class DanmuService {

    private static final String DANMU_KEY = "dm-video-";

    @Autowired
    private DanmuDao danmuDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 添加弹幕
     * @param danmu
     */
    public void addDanmu(Danmu danmu){
        danmuDao.addDanmu(danmu);
    }

    /**
     * 获取弹幕(登录用户可筛选时间)
     * @param videoId 视频Id
     * @param startTime 筛选开始时间
     * @param endTime 筛选结束时间
     * @return 筛选弹幕结果
     * @throws Exception
     */
    public List<Danmu> getDanmus(Long videoId,
                                 String startTime,
                                 String endTime) throws Exception {
        String key = DANMU_KEY + videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> list;
        if (!StringUtil.isNullOrEmpty(value)) {
            list = JSONArray.parseArray(value, Danmu.class);
            if (!StringUtil.isNullOrEmpty(startTime) && !StringUtil.isNullOrEmpty(endTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                List<Danmu> childList = new ArrayList<>();
                for (Danmu danmu : list){
                    Date createTime = danmu.getCreateTime();
                    if (createTime.after(startDate) && createTime.before(endDate)){
                        childList.add(danmu);
                    }
                }
                list = childList;
            }
        }else{
            Map<String, Object> params = new HashMap<>();
            params.put("videoId", videoId);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            list = danmuDao.getDanmus(params);
            // 保存弹幕到Redis
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
        }
        return list;
    }

    /**
     * 添加弹幕到Redis
     * @param danmu
     */
    public void addDanmusToRedis(Danmu danmu){
        String key = DANMU_KEY + danmu.getVideoId();
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> list = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(value)) {
            list = JSONArray.parseArray(value, Danmu.class);
        }
        list.add(danmu);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));

    }

    @Async
    public void asyncAddDanmu(Danmu danmu) {
        danmuDao.addDanmu(danmu);
    }
}
