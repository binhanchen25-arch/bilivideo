package com.bilibili.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author: Dk.L
 * @date: 2023/3/25
 * @Description: TODO
 */
@Component
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DataLimited {

}
