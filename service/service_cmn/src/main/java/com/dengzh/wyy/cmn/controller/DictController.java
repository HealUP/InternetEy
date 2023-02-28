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

    // 导入数据字典
    @PostMapping("importData")
    public Result importDict(MultipartFile file) { // 传入excel文件
        dictService.importDictData(file);
        return Result.ok();
    }
    // 导出数据字典接口
    @ApiOperation(value = "导出数据字典")
    @GetMapping( "/exportData")
    public void exportData(HttpServletResponse response) {
             dictService.exportData(response);// 不用返回数据
    }
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
