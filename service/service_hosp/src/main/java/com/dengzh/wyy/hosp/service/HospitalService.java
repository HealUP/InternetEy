package com.dengzh.wyy.hosp.service;

import com.dengzh.wyy.model.hosp.Hospital;

import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> paramMap);


    Hospital getByHoscode(String hoscode);
}
