package com.bilibili.service;

import com.bilibili.dao.repository.UserInfoRepository;
import com.bilibili.dao.repository.VideoRepository;
import com.bilibili.domain.UserInfo;
import com.bilibili.domain.Video;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: Dk.L
 * @date: 2023/4/1
 * @Description: ElasticSearch service层
 */
@Service
public class ElasticSearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void addUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    public List<Map<String, Object>> getContents(String keyword, Integer pageNo, Integer pageSize) throws IOException {
        String[] indices = {"videos", "user-infos"};
        SearchRequest searchRequest = new SearchRequest(indices);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 分页
        sourceBuilder.from(pageNo - 1);
        sourceBuilder.size(pageSize);
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "nick", "description");
        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 高亮显示
        String[] array = {"title", "nick", "description"};
        HighlightBuilder highLightBuilder = new HighlightBuilder();
        for (String key : array) {
            highLightBuilder.fields().add(new HighlightBuilder.Field(key));
        }
        // 如果要多个字段进行高亮，要设置为false
        highLightBuilder.requireFieldMatch(false);
        highLightBuilder.preTags("<span style=\"color:red\">");
        highLightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highLightBuilder);
        // 执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> arrayList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            // 处理高亮字段
            Map<String, HighlightField> highLightBuilderFields = hit.getHighlightFields();
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            for (String key : array) {
                HighlightField field = highLightBuilderFields.get(key);
                if (field != null) {
                    Text[] fragments = field.fragments();
                    String str = Arrays.toString(fragments);
                    str = str.substring(1, str.length() - 1);
                    sourceMap.put(key, str);
                }
            }
            arrayList.add(sourceMap);
        }
        return arrayList;
    }

    public void addVideo(Video video) {
        videoRepository.save(video);
    }

    public Video getVideo(String keyword) {
        return videoRepository.findByTitleLike(keyword);
    }

    public void deleteAllVideo(){
        videoRepository.deleteAll();
    }

}
