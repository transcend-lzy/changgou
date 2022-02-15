package com.changgou.item.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author chaoyue
 * @data2022-02-09 10:08
 */
@Configuration
public class EnableMvcConfig implements WebMvcConfigurer {
    /***
     * 静态资源过滤
     * 原来springmvc的设置方法
     * mapping:请求路径映射
     * location：本地查找路径
     * <mvc:resources mapping=" location=""/>
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/items/**").    //mapping
                addResourceLocations("classpath:/templates/items/");   //location  去哪里找静态页
    }
}
