package com.bilibili.dao.repository;

import com.bilibili.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author: Dk.L
 * @date: 2023/4/1
 * @Description: TODO
 */
public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, Long> {

}

