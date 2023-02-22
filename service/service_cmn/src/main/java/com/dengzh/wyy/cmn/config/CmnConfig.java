package com.dengzh.wyy.cmn.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* Description: 配置文件
* date: 2023/2/22 9:59
 *
* @author: Deng
* @since JDK 1.8
*/
@Configuration
@MapperScan("com.dengzh.wyy.cmn.mapper")
public class CmnConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
