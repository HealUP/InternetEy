package com.dengzh.wyy.hosp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dengzh.wyy.hosp.mapper.HospitalSetMapper;
import com.dengzh.wyy.hosp.service.HospitalSetService;
import com.dengzh.wyy.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper,HospitalSet> implements HospitalSetService {


//    @Override
//    public boolean updateByObj(updateHospitalSet updatehospitalSet) {
//        baseMapper.updateById(updatehospitalSet);
//        return false;
//    }

}
