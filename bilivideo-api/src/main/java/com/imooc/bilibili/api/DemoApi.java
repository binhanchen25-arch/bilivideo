package com.imooc.bilibili.api;

import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.Video;
import com.bilibili.service.ElasticSearchService;
import com.bilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Dk.L
 * @date: 2023/4/1
 * @Description: ElasticSearch 测试类
 */
@RestController
public class DemoApi {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @GetMapping("/es-videos")
    public JsonResponse<Video> getEsVideos(@RequestParam String keyword){
        Video video = elasticSearchService.getVideo(keyword);
        return new JsonResponse(video);
    }
}
