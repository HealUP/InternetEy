package com.dengzh.wyy.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dengzh.wyy.cmn.client.DictFeignClient;
import com.dengzh.wyy.enums.DictEnum;
import com.dengzh.wyy.hosp.repository.HospitalRepository;
import com.dengzh.wyy.hosp.service.HospitalService;
import com.dengzh.wyy.model.hosp.Hospital;
import com.dengzh.wyy.model.vo.hosp.HospitalQueryVo;
import org.apache.catalina.Host;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 1.添加医院信息到模拟前台系统,将医院数据持久化到MongOnDB数据库
 * 2.通过医院编号获取医院信息(医院前台模拟系统)
 * 3.条件查询带分页查询医院信息(wyy后台管理系统),从mongodb数据库中查询数据
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    // 添加医院数据
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

    // 通过医院编号获取医院
    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    // 条件查询带分页查询医院信息
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        // 创建pageable对象
        Pageable pageable = PageRequest.of(page - 1, limit);
        // 创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 模糊查询
                .withIgnoreCase(true); // 忽略大小写

        // hospitalQueryVo 转化为 hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        // 创建Example对象
        Example<Hospital> example = Example.of(hospital, matcher);
        // 调用方法实现查询医院信息
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        // 进一步，查询医院的等级信息 给hospital设置医院等级信息 需要调用service_cmn微服务模块

        // 进行医院等级封装(设置到hospital对象中) pages里面遍历的是hospital
        pages.getContent().stream().forEach(item -> {
            this.setHospitalHosType(item); // 调用医院等级,省,市,地区封装的方法
        });
        return pages;
    }

    // 更新医院状态 mongodb中的更新
    @Override
    public void updateStatus(String id, Integer status) {
        // 根据id查询医院的信息
        Hospital hospital = hospitalRepository.findById(id).get();
        // 设置修改的值
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        // 重新添加到mongodb中
        hospitalRepository.save(hospital);
    }

    // 进行医院等级,省,市,地区的封装(设置到hospital对象中)
    private Hospital setHospitalHosType(Hospital hospital) {
        // 远程调用 查询医院等级 mongodb中的hostype对应数据字典的dictCode字段
        String hostypeString = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), hospital.getHostype()); // Hostype枚举值作为dictCode值，获取hostype 作为value值（观察mongodb可知）

        // 查询省,市 地区，地址 只需要根据value查询即可
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode()); // value
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        // 放入到对象中
        hospital.getParam().put("hostypeString", hostypeString); // 医院等级信息   获取到map对象后插入数据
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress()); // 医院的具体地址
        return hospital;
    }


}
