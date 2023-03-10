package com.dengzh.wyy.hosp.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* Description: 实现动态扫描mapper包
* date: 2023/2/15 22:46
 * 可以加到启动类
* @author: Deng
* @since JDK 1.8
*/
@Configuration
@MapperScan("com.dengzh.wyy.hosp.mapper")
public class HospConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
