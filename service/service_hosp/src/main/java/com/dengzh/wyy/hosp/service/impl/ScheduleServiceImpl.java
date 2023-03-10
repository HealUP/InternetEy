package com.dengzh.wyy.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dengzh.wyy.hosp.repository.ScheduleRepository;
import com.dengzh.wyy.hosp.service.DepartmentService;
import com.dengzh.wyy.hosp.service.HospitalService;
import com.dengzh.wyy.hosp.service.ScheduleService;
import com.dengzh.wyy.model.hosp.Schedule;
import com.dengzh.wyy.model.vo.hosp.BookingScheduleRuleVo;
import com.dengzh.wyy.model.vo.hosp.ScheduleQueryVo;

import jdk.nashorn.internal.runtime.WithObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // 更加方便使用mongodb
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;
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

    //根据医院编号和科室编号，查询排班规则数据
    @Override
    public Map<String, Object> getRuleSchedule(Integer page, Integer limit, String hoscode, String depcode) {
        // 1 根据医院编号 科室编号 进行查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        // 2 根据工作日期workDate进行分组
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria), // 匹配条件
                Aggregation.group("workDate") // 分组字段
                .first("workDate").as("workDate")// 别名还是workDate
                // 3.统计号源数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")  // 总数 预存
                        .sum("availableNumber").as("availableNumber"), // 可用
                // 排序
                Aggregation.sort(Sort.Direction.DESC,"workDate"), // 可预约日期
                // 4. 实现分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit));
        // 调用方法，最终执行
        AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregationResults.getMappedResults(); // 得到list集合


        // 根据workDate分组查询总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalResults = mongoTemplate.aggregate(totalAgg,Schedule.class, BookingScheduleRuleVo.class);
        int total = totalResults.getMappedResults().size(); // 总数

        // 获取日期对应的星期
        for(BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            //调用方法 获取星期几 遍历返回的数据 替换原始数据中的星期几字段
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            // 调用日期换算成星期的方法
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek); // 替换原始数据中的星期几字段 设置回去
        }

        // 设置最终数据，进行返回
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        result.put("total", total);
        // 获取医院名称
        String hosname = hospitalService.getHospNameByHoscode(hoscode);
        Map<String, String> baseMap = new HashMap<>(); // 再封装一次 map 里面包着map
        baseMap.put("hosname", hosname);
        result.put("baseMap", baseMap);
        return result;
    }

    // 根据医院编号和科室编号查询排班规则数据
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        // 根据参数查询mongodb得到scheduleList排班规则数据
        // 这里要将DateTime类型的数据转化为Date类型 toDate()方法
        List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        //使用java8的语法 用流把得到scheduleList集合遍历，向里面设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.stream().forEach(item -> {
            this.pageSchedule(item); // 每次遍历就传入一个集合 调用封装医院名称、科室名称、日期对应星期信息的方法  item是一个集合 包含了多个Schedule类型的数据
        });
        return scheduleList;
    }

    // 封装医院名称、科室名称、日期对应星期信息的方法
    private void pageSchedule(Schedule schedule) {
        /*
        * 逻辑：使用schedule实体类继承的basemongoEntity实体类中的 Param参数存放额外的需要的字段
        * */
        // 封装医院名称
        schedule.getParam().put("hosname", hospitalService.getHospNameByHoscode(schedule.getHoscode())); // 根据医院编号查询医院名称
        // 封装科室名称
        schedule.getParam().put("depname", departmentService.getDepartmentName(schedule.getHoscode(), schedule.getDepcode())); // 根据医院编号和科室编号获取科室名称
        // 封装日期对应的星期 需要调用日期转换为星期的方法
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate()))); // 这里的workDate时String类型的
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }


}
