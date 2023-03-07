package com.dengzh.wyy.cmn.client;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 远程服务调用配置
 * 注意点:
 * 1. 方法直接从controller复制,但是路径要补全
 * 2. @PathVariable中要指定对应的参数,不指定会出问题
 */
@Repository
@FeignClient("service-cmn") // 调用的微服务模块 名字要正确,看配置文件
public interface DictFeignClient {

    // 根据dictCode和value查询name
    @ApiOperation(value = "获取数据字典名称,根据dictcode,value查询")
    @GetMapping("/admin/cmn/dict/getName/{dictCode}/{value}")
    public String getName(
            @ApiParam(name = "dictCode", value = "上级编码", required = true)
            @PathVariable("dictCode") String dictCode,
            @ApiParam(name = "value", value = "值", required = true)
            @PathVariable("value") String value);

    // 根据value查询
    @ApiOperation(value = "获取数据字典名称,根据value查询")
    @GetMapping("/admin/cmn/dict/getName/{value}")
    public String getName(
            @ApiParam(name = "value", value = "值")
            @PathVariable("value") String value);

}
