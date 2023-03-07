package com.dengzh.wyy.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableSwagger2
@EnableDiscoveryClient // 服务注册
@EnableFeignClients(basePackages = "com.dengzh") // 扫描配置的FeignClientservice_cmn
@ComponentScan(basePackages = "com.dengzh.wyy")//设置扫描规则：一启动就扫描该路径下的包 其中该包下包含swagger2的配置
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
