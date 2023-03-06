package com.dengzh.wyy.hosp.repository;


import com.dengzh.wyy.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {

    // 根据编号找到医院
    Hospital getHospitalByHoscode(String hoscode);
}
