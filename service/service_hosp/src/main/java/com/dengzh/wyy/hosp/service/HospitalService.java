package com.dengzh.wyy.hosp.service;

import com.dengzh.wyy.model.hosp.Hospital;
import com.dengzh.wyy.model.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    // 上传医院数据
    void save(Map<String, Object> paramMap);

    // 通过医院编号获取医院
    Hospital getByHoscode(String hoscode);

    // 条件查询带分页 查询医院信息
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    // 更新医院状态
    void updateStatus(String id, Integer status);

    // 根据id查找医院
    Map<String, Object> getHospById(String id);

    // 根据医院编号获取医院名称
    String getHospNameByHoscode(String hoscode);

    // 根据医院名称查询医院信息
    List<Hospital> findByHosname(String hosname);
}
