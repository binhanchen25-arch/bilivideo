package com.imooc.bilibili.api;

import com.bilibili.domain.Danmu;
import com.bilibili.domain.JsonResponse;
import com.bilibili.service.DanmuService;
import com.imooc.bilibili.api.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: Dk.L
 * @date: 2023/3/31
 * @Description: 弹幕控制层
 */
@RestController
public class DanmuApi {

    @Autowired
    private DanmuService danmuService;

    @Autowired
    private UserSupport userSupport;

    // 获取弹幕(登录用户可筛选时间)
    @GetMapping("/danmus")
    public JsonResponse<List<Danmu>> getDanmus(@RequestParam Long videoId,
                                               String startTime,
                                               String endTime) throws Exception {
        List<Danmu> list;
        try{
            // 判断当前是游客模式还是用户登录模式
            userSupport.getCurrentUserId();
            // 若是用户登陆模式，则允许用户进行时间段筛选
            list = danmuService.getDanmus(videoId, startTime, endTime);
        }catch(Exception ignored){
            // 若为游客模式，则不允许用户进行时间段筛选
            list = danmuService.getDanmus(videoId, null, null);
        }
        return new JsonResponse(list);
    }
}
