package com.imooc.bilibili.api;

import com.bilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author: Dk.L
 * @date: 2023/3/28
 * @Description: Restful风格，测试类
 */
@RestController
public class RESTfulApi {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    // 文件分片
    @GetMapping("/slices")
    public void slices(MultipartFile file) throws Exception{
        fastDFSUtil.convertFileToSlices(file);
    }

}
