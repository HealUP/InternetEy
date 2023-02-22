package com.dengzh.wyy.cmn.controller;

import com.dengzh.wyy.cmn.service.DictService;
import com.dengzh.wyy.common.result.Result;
import com.dengzh.wyy.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "数据字典接口管理")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin //允许跨域访问
public class DictController {

    @Autowired
    private DictService dictService;

    // 根据id查询子数据列表
    @ApiOperation(value = "根据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(
            @ApiParam(name = "id", value = "id号", required = true)
            @PathVariable Long id) {
        List<Dict> list = dictService.findChildData(id);// 调用重写的方法
        return Result.ok(list);
    }


}
