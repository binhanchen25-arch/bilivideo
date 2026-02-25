package com.bilibili.dao;

import com.bilibili.domain.File;
import org.apache.ibatis.annotations.Mapper;



/**
 * @author: Dk.L
 * @date: 2023/3/29
 * @Description: 文件Dao层
 */
@Mapper
public interface FileDao {

    Integer addFile(File file);

    File getFileByMD5(String md5);


}
