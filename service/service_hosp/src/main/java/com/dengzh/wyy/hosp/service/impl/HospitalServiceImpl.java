package com.dengzh.wyy.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dengzh.wyy.hosp.repository.HospitalRepository;
import com.dengzh.wyy.hosp.service.HospitalService;
import com.dengzh.wyy.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 将医院数据持久化到MongOnDB数据库
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        // 思路：添加到mongondb数据库 先判断数据库中是否存在该医院，如果存在则进行更新，如果不存在则进行添加 （针对一些字段）

        //把参数map转化为对象  步骤:json——>字符串——>对象
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);// 指定要转化为的对象 是Hospital
        // 判断数据库是否存在相同的数据
        String hoscode = hospital.getHoscode();  // 先查询mysql数据库
        Hospital hospitalExit = hospitalRepository.getHospitalByHoscode(hoscode);
        // 如果存在，则进行修改
        if (hospitalExit != null) {
            hospital.setStatus(hospitalExit.getStatus());
            hospital.setCreateTime(hospitalExit.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital); // 存入数据库
        } else {
            // 如果不存在， 则进行添加
            //0：未上线 1：已上线
            hospital.setStatus(0); // moren
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }
}
