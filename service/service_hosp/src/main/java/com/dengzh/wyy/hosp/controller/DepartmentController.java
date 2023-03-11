package com.dengzh.wyy.hosp.controller;

import com.dengzh.wyy.common.result.Result;
import com.dengzh.wyy.hosp.service.DepartmentService;
import com.dengzh.wyy.model.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 医院科室信息
 * date: 2023/3/9 13:51
 *
 * @author: Deng
 * @since JDK 1.8
 */

@Api(tags = "科室管理接口")
//@CrossOrigin
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    // 根据医院编号查询所有科室的列表
    @ApiOperation("查询所有科室的列表")
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(
            @ApiParam(name = "hoscode", value = "医院编号")
            @PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list); // 返回list集合
    }
}
