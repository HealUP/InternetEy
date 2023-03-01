package com.dengzh.wyy.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dengzh.wyy.hosp.mapper.HospitalSetMapper;
import com.dengzh.wyy.hosp.service.HospitalSetService;
import com.dengzh.wyy.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper,HospitalSet> implements HospitalSetService {
    @Override
    public String getSignKey(String hoscode) {
        // 根据医院编号，查询签名
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);

        return hospitalSet.getSignKey();// 获取签名
    }

}
