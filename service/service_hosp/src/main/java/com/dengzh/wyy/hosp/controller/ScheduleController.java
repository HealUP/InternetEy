package com.dengzh.wyy.hosp.controller;


import com.dengzh.wyy.common.result.Result;
import com.dengzh.wyy.hosp.service.ScheduleService;
import com.dengzh.wyy.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "排班管理")
@RestController
@CrossOrigin
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // 根据医院编号和科室编号查询排班规则数据
    @ApiOperation("查询排班规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(
            @ApiParam(name = "page", value = "当前页", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "页面大小", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "hoscode", value = "医院编号", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室编号", required = true)
            @PathVariable String depcode
    ) {
        Map<String, Object> map = scheduleService.getRuleSchedule(page, limit, hoscode, depcode); // 数据中很多内容，所以返回map比较方便
        return Result.ok(map);
    }

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate) {
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode, depcode, workDate); // 返回封装号的数据u
        return Result.ok(list);
    }
}
