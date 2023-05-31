package com.dengzh.wyy.hosp.controller.api;

import com.dengzh.wyy.common.result.Result;
import com.dengzh.wyy.hosp.service.HospitalService;
import com.dengzh.wyy.model.hosp.Hospital;
import com.dengzh.wyy.model.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台用户系统调用接口
 */

@RestController
@RequestMapping("/api/hosp/hospital")
@Api("用户前台系统医院管理接口")
public class HospApiController {

    @Autowired
    private HospitalService hospitalService;

    @ApiOperation("条件查询带分页 查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(
            @ApiParam(value = "page", name = "当前页", required = true)
            @PathVariable int page,
            @ApiParam(value = "limit", name = "页面大小", required = true)
            @PathVariable int limit,
            @ApiParam(value = "hospitalQueryVo", name = "搜索对象", required = false) //  不是必须参数
                    HospitalQueryVo hospitalQueryVo
    ) {
        Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        List<Hospital> content = hospitals.getContent(); // 对应前端获取的字段  医院列表
        int totalPages = hospitals.getTotalPages();// 对应前端获取的页数
        return Result.ok(hospitals);
    }


    @ApiOperation("根据医院名称查询医院信息")
    @GetMapping("findByHosName/{hosname}")
    public Result findByHosName(
            @ApiParam(value = "hosname", name = "医院名称", required = true)
            @PathVariable String hosname
    ) {

        List<Hospital> list = hospitalService.findByHosname(hosname);
        return Result.ok(list);

    }

}
