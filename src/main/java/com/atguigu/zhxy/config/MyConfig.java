package com.atguigu.zhxy.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *@Author: feifan
 *@Date: 2022/6/6 21:04
 */
@Configuration
@MapperScan("com.atguigu.zhxy.mapper")
public class MyConfig {

    /**
     * 分页插件
     * @return paginationInterceptor
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        return paginationInterceptor;
    }

}
