package com.dengzh.wyy.hosp.controller;

import com.dengzh.wyy.common.result.Result;
import com.dengzh.wyy.hosp.service.HospitalService;
import com.dengzh.wyy.model.hosp.Hospital;
import com.dengzh.wyy.model.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Api(tags = "医院管理接口")
@RestController
@CrossOrigin
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    // 获取医院信息接口 分为:1 条件查询带分页 2 根据dictCode和value查询数据字典名称
    // 1 条件查询带分页
    @ApiOperation("获取医院信息分页列表")
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "hospitalQueryVo", value = "查询对象", required = false)
            HospitalQueryVo hospitalQueryVo
    ) {
        Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(pageModel);
    }

}
