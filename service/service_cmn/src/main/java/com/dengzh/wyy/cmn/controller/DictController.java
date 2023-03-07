package com.dengzh.wyy.cmn.controller;

import com.dengzh.wyy.cmn.service.DictService;
import com.dengzh.wyy.common.result.Result;
import com.dengzh.wyy.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典接口管理")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin //允许跨域访问
public class DictController {

    @Autowired
    private DictService dictService;

    // 导入数据字典接口
    @PostMapping("importData")
    public Result importDict(MultipartFile file) { // 传入excel文件
        dictService.importDictData(file);
        return Result.ok();
    }

    // 导出数据字典接口
    @ApiOperation(value = "导出数据字典")
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);// 不用返回数据
    }

    // 根据id查询子数据列表接口
    @ApiOperation(value = "根据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(
            @ApiParam(name = "id", value = "id号", required = true)
            @PathVariable Long id) {
        List<Dict> list = dictService.findChildData(id);// 调用重写的方法
        return Result.ok(list);
    }

    // 根据dictCode和value查询name
    @ApiOperation(value = "获取数据字典名称,根据dictcode,value查询")
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(
            @ApiParam(name = "dictCode", value = "上级编码", required = true)
            @PathVariable String dictCode,
            @ApiParam(name = "value", value = "值", required = true)
            @PathVariable String value) {
        String dictName = dictService.getDictName(dictCode, value);
        return dictName;
    }

    // 根据value查询
    @ApiOperation(value = "获取数据字典名称,根据value查询")
    @GetMapping("getName/{value}")
    public String getName(
            @ApiParam(name = "value", value = "值")
            @PathVariable String value) {
        String dictName = dictService.getDictName("",value);
        return  dictName;
    }


}
