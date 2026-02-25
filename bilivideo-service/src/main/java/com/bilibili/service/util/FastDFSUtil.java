package com.bilibili.service.util;

import com.bilibili.domain.exception.ConditionException;
import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * @author: Dk.L
 * @date: 2023/3/27
 * @Description: FastDFS工具
 */
@Component
public class FastDFSUtil {

    @Value("${fdfs.http.storage-addr}")
    private String httpFdfsStorageAddr;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String PATH_KEY = "path-key:";

    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";

    private static final String UPLOADED_NO_KEY = "uploaded-no-key:";

    private static final String DEFAULT_GROUP = "group1";

    private static final int SLICE_SIZE = 1024 * 1024;

    // 获取文件类型  'test.png' -> 'png'
    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件！");
        }
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1);
    }

    // 上传文件
    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = this.getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }

    // 上传可以断点续传的文件
    public String uploadAppenderFile(MultipartFile file) throws Exception {
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    // 修改续传文件的内容
    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws Exception {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }

    // 通过分片上传文件
    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        if (file == null || sliceNo == null || totalSliceNo == null) {
            throw new ConditionException("参数异常！");
        }
        // 1. 文件唯一标识
        String pathKey = PATH_KEY + fileMd5;
        // 2. 目前上传所有分片的总大小：offset
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
        // 3. 目前上传的分片数
        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;

        // 上传分片总大小：如果是第1个分片大小为0, 其他则获取已上传的总大小
        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize = 0L;
        if (!StringUtil.isNullOrEmpty(uploadedSizeStr)) {
            uploadedSize = Long.valueOf(uploadedSizeStr);
        }

        // 上传的是第一个分片
        if (sliceNo == 1) {
            // 获取第一个分片的文件路径
            String path = this.uploadAppenderFile(file);
            if (StringUtil.isNullOrEmpty(path)) {
                throw new ConditionException("上传失败！");
            }
            redisTemplate.opsForValue().set(pathKey, path);
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
        } else {
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if (StringUtil.isNullOrEmpty(filePath)) {
                throw new ConditionException("上传失败！");
            }
            this.modifyAppenderFile(file, filePath, uploadedSize);
            // 分片序号 +1
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }

        // 修改历史上传分片文件大小
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));

        // 如果所有分片全部上传完毕，则清空redis里面相关的key和value
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
        String resultPath = "";
        // 全部分片上传完成
        if (uploadedNo.equals(totalSliceNo)) {
            resultPath = redisTemplate.opsForValue().get(pathKey);
            List<String> keyList = Arrays.asList(uploadedNoKey, pathKey, uploadedSizeKey);
            redisTemplate.delete(keyList);
        }
        return resultPath;
    }

    /**
     * 文件转换
     * @param multipartFile spring提供的文件类型
     * @throws Exception
     */
    public void convertFileToSlices(MultipartFile multipartFile) throws Exception {
        String fileType = this.getFileType(multipartFile);
        // 生成临时文件，将 MultipartFile -> File
        File file = this.multipartFileToFile(multipartFile);
        long fileLength = file.length();
        int count = 1;
        for (int i = 0; i < fileLength; i += SLICE_SIZE) {
            // 定位分片起始位置
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
            // 如果是最后一个分片，大小不为指定分片大小，len用于表示最后一个分大小
            int len = randomAccessFile.read(bytes);
            String path = "D:\\tmpfile\\" + count + "." + fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes, 0, len);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        // 删除临时文件
        file.delete();
    }

    // 文件类型转换 MultipartFile -> File
    public File multipartFileToFile(MultipartFile multipartFile) throws Exception {
        String originalFileName = multipartFile.getOriginalFilename();
        String[] fileName = originalFileName.split("\\.");
        File file = File.createTempFile(fileName[0], "." + fileName[1]);
        multipartFile.transferTo(file);
        return file;
    }


    // 删除文件
    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }

    // 视频在线播放
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                                HttpServletResponse response,
                                                String path) throws Exception{
        // 文件信息
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        // 文件总大小
        long totalFileSize = fileInfo.getFileSize();
        // 文件服务器上的真实访问路径 ： 'http://xxx:8888/group1/M00/00/00/xxx.png'
        String url = httpFdfsStorageAddr + path;
        // 获取请求头的所有name值
        Enumeration<String> headerNames = request.getHeaderNames();
        // headers 存放请求头信息
        Map<String, Object> headers = new HashMap<String, Object>();
        while(headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            // getHeader(name) 获取单个请求头 name -> value 值
            headers.put(header, request.getHeader(header));
        }
        // 当前分片的range开始和结束位置
        String rangeStr = request.getHeader("Range");
        String[] range;
        if (StringUtil.isNullOrEmpty(rangeStr)) {
            rangeStr = "bytes=0-" + (totalFileSize - 1);
        }
        // 获取分片的begin和end
        range = rangeStr.split("bytes=|-");
        long begin = 0;
        // 当为2时只有开始位置，没有终止位置
        if(range.length >= 2){
            // range[0]第一个数据是一个空的数据，所以此处选择range[1]
            begin = Long.parseLong(range[1]);
        }
        long end = totalFileSize - 1;
        // 当为3时，是有终止位置的
        if(range.length >= 3){
            end = Long.parseLong(range[2]);
        }
        // 获取分片长度
        long len = (end - begin) + 1;
        // 告诉前端具体返回的range的范围： bytes 0-800/801 -> 开始-终止/文件总大小
        String contentRange = "bytes " + begin + "-" + end + "/" + totalFileSize;
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int) len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        // 发送 get 请求, 获取流数据, 写入 response
        HttpUtil.get(url, headers, response);
    }
}
