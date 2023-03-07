package com.dengzh.wyy.hosp.repository;

import com.dengzh.wyy.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {

    // 根据医院编号和科室编号查询科室的信息
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
