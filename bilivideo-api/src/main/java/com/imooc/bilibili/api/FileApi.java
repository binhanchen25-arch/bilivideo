package com.imooc.bilibili.api;

import com.bilibili.domain.JsonResponse;
import com.bilibili.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: Dk.L
 * @date: 2023/3/28
 * @Description: File controller层
 */
@RestController
public class FileApi {

    @Autowired
    private FileService fileService;

    // 获取文件 md5 加密后的字符串
    @PostMapping("/md5files")
    public JsonResponse getFileMD5(MultipartFile multipartFile) throws Exception {
        String fileMD5 = fileService.getFileMD5(multipartFile);
        return new JsonResponse(fileMD5);
    }

    // 分片上传文件
    @PutMapping("/file-slices")
    public JsonResponse<String> uploadFileBySlices(MultipartFile slice,
                                                   String fileMd5,
                                                   Integer sliceNo,
                                                   Integer totalSliceNo) throws Exception {
        String filePath = fileService.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
        return new JsonResponse(filePath);
    }
}
