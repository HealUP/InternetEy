package com.dengzh.wyy.hosp.repository;

import com.dengzh.wyy.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
* Description: 按照springData规范写，mongoDB会自动根据该方法描述的语义执行
* date: 2023/3/10 17:35
 *
* @author: Deng
* @since JDK 1.8
*/

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    // 根据医院编号和排班编号查询排班信息
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    // 根据医院编号和科室编号查询排班规则数据
    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);
}
