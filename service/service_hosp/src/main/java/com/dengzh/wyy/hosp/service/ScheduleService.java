package com.dengzh.wyy.hosp.service;


import com.dengzh.wyy.model.hosp.Schedule;
import com.dengzh.wyy.model.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    // 前端模拟系统上传排班接口
    void save(Map<String, Object> paramMap);

    // 前端模拟系统查询排班接口
    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    //前端模拟系统删除排班接口
    void remove(String hoscode, String hosScheduleId);

    // 根据医院编号和科室编号查询排班规则数据
    Map<String, Object> getRuleSchedule(Integer page, Integer limit, String hoscode, String depcode);

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);
}
