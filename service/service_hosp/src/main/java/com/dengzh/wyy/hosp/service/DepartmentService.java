package com.dengzh.wyy.hosp.service;

import com.dengzh.wyy.model.hosp.Department;
import com.dengzh.wyy.model.vo.hosp.DepartmentQueryVo;
import com.dengzh.wyy.model.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    // 上传科室接口
    void save(Map<String, Object> paramMap);

    // 模糊查询带分页 查询医院列表
    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    // 删除科室接口
    void remove(String hoscode, String depcode);

    // 根据医院编号查询所有科室的列表
    List<DepartmentVo> findDeptTree(String hoscode);

    // 根据医院编号和科室编号获取科室名称
    String getDepartmentName(String hoscode, String depcode);
}

