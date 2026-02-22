package com.bilibili.dao.repository;

import com.bilibili.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author: Dk.L
 * @date: 2023/4/1
 * @Description: TODO
 */
public interface VideoRepository extends ElasticsearchRepository<Video, Long> {
    Video findByTitleLike(String keyword);
}
