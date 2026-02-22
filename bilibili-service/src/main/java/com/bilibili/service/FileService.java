package com.bilibili.service;

import com.bilibili.dao.FileDao;
import com.bilibili.domain.File;
import com.bilibili.service.util.FastDFSUtil;
import com.bilibili.service.util.MD5Util;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @author: Dk.L
 * @date: 2023/3/28
 * @Description: File service 层
 */
@Service
public class FileService {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private FileDao fileDao;

    /**
     * 分片上传文件
     *
     * @param slice        分片文件
     * @param fileMD5      文件MD5值
     * @param sliceNo      分片编码
     * @param totalSliceNo 总分片数量
     * @return
     * @throws Exception
     */
    public String uploadFileBySlices(MultipartFile slice,
                                     String fileMD5,
                                     Integer sliceNo,
                                     Integer totalSliceNo) throws Exception {
        // 如果该文件存在，直接返回（秒传）
        File dbFileMD5 = fileDao.getFileByMD5(fileMD5);
        if (dbFileMD5 != null){
            return dbFileMD5.getUrl();
        }
        // 分片上传文件
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);

        // 数据库插入文件信息
        if (!StringUtil.isNullOrEmpty(url)) {
            dbFileMD5 = new File();
            dbFileMD5.setCreateTime(new Date());
            dbFileMD5.setMd5(fileMD5);
            dbFileMD5.setUrl(url);
            dbFileMD5.setType(fastDFSUtil.getFileType(slice));
        }
        return url;
    }
    public String getFileMD5(MultipartFile file) throws Exception{
        return MD5Util.getFileMD5(file);
    }
}
