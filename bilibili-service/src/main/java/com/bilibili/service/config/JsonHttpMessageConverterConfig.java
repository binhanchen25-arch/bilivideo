package com.bilibili.service.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: Dk.L
 * @date: 2023/3/20
 * @Description: JSON配置类
 */
@Configuration
public class JsonHttpMessageConverterConfig {

    // 理解分循环引用的作用
    // public static void main(String[] args) {
    //     List<Object> list = new ArrayList<>();
    //     Object o = new Object();
    //     list.add(o);
    //     list.add(o);
    //     System.out.println(list.size());
    //     System.out.println(JSONObject.toJSONString(list));
    //     System.out.println(JSONObject.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));
    // }

    @Bean
    @Primary
    public HttpMessageConverters fastJsonHttpMessageConverters(){
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd  HH:mm:ss");
        // 设置序列化规则，依次为：格式化、字符串null写为Empty、listNull写为empty、写入Map空值、map排序、禁止循环引用
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.MapSortField,
                SerializerFeature.DisableCircularReferenceDetect
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);
        // 如果使用feign进行微服务间的接口调用，则需要加上该配置
        // fastConverter.setSupportedMediaTypes(Collection.singletonList(PageAttributes.MediaType.APPLICATION_JSON))
        return new HttpMessageConverters(fastConverter);
    }
}
