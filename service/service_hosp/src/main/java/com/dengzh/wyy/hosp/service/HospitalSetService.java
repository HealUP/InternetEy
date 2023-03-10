package com.dengzh.wyy.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dengzh.wyy.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {

     String  getSignKey(String hoscode);
}
