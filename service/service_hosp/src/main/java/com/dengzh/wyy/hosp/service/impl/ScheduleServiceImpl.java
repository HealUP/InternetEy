package com.dengzh.wyy.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dengzh.wyy.hosp.repository.ScheduleRepository;
import com.dengzh.wyy.hosp.service.ScheduleService;
import com.dengzh.wyy.model.hosp.Schedule;
import com.dengzh.wyy.model.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // 上传排班接口
    @Override
    public void save(Map<String, Object> paramMap) {

        // json转换为对象
        String paramString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramString, Schedule.class);

        // 根据医院编号和排班编号查询排班
        Schedule scheduleExit = scheduleRepository
                .getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId()); // springData整合了mongndgb

        // 判断当前排班是否存在
        if (scheduleExit != null) { // 存在则更新信息
            scheduleExit.setUpdateTime(new Date());
            scheduleExit.setIsDeleted(0);
            scheduleExit.setStatus(1);
            scheduleRepository.save(scheduleExit); // 插入到mongodb数据库 mongodb自带的save方法
        } else { // 不存在则添加当前传来的对象
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }

    }

    // 查询排班接口
    @Override
    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        // 配置Pageable对象 设置当前页和每页的记录数 （mongodb的分页）
        // 0 是第一页
        PageRequest pageable = PageRequest.of(page - 1, limit);
        // 创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 进行模糊查询
                .withIgnoreCase(true); // 忽略大小写
        // 转换dScheduleQueryVo对象 为 department对象
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, scheduleQueryVo); // 将dscheduleQueryVo对象 复制给 schedule
        // Example对象
        Example<Schedule> example = Example.of(schedule, matcher); // 此处的department对象要从departmentQueryVo中转换过来
        Page<Schedule> all = scheduleRepository.findAll(example, pageable);
        return all;
    }

    // 删除排班接口
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        // 先查询 根据医院编号和排班编号
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            // 调用方法删除
            scheduleRepository.deleteById(schedule.getId()); // 根据id删除
        }
    }

}
