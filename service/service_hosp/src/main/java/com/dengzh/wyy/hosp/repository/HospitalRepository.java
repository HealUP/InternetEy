package com.dengzh.wyy.hosp.repository;


import com.dengzh.wyy.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 按照springData的命名规范写方法名，mongoDB直接帮我们实现该方法
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {

    // 根据编号找到医院
    Hospital getHospitalByHoscode(String hoscode);

    // 根据医院名称查询医院信息
    List<Hospital> findHospitalByHosnameLike(String hosname);
}
