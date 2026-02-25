package com.bilibili.dao;

import com.bilibili.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author: Dk.L
 * @date: 2023/3/31
 * @Description: 弹幕Dao曾
 */
@Mapper
public interface DanmuDao {

    Integer addDanmu(Danmu danmu);

    List<Danmu> getDanmus(Map<String, Object> params);
}
