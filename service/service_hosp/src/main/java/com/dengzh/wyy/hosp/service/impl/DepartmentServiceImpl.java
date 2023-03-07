package com.dengzh.wyy.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dengzh.wyy.hosp.repository.DepartmentRepository;
import com.dengzh.wyy.hosp.service.DepartmentService;
import com.dengzh.wyy.model.hosp.Department;
import com.dengzh.wyy.model.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 科室数据 上传至mongodb数据库、以及查询、删除接口
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    DepartmentRepository departmentRepository;

    // 上传科室的接口
    @Override
    public void save(Map<String, Object> paramMap) {

        //1 将json字符串格式的数据转换为对象
        String mapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(mapString, Department.class); // Hospital.class 指定转化的对象

        // 2 判断数据库是否存在相同的数据
        Department departmentExit = departmentRepository
                .getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode()); // 根据mongodb的命名规范命名 根据科室编号，医院编号查询
        // 判断是否存在
        if (departmentExit != null) { // 存在就更新数据 将查询出来的科室的信息的时间、和逻辑删除字段进行更新
            departmentExit.setUpdateTime(new Date());
            departmentExit.setIsDeleted(0);
            departmentRepository.save(departmentExit);
        } else { // 不存在则将得到的对象存入
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    // 查询科室数据接口
    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        // 配置Pageable对象 设置当前页和每页的记录数 （mongodb的分页）
        // 0 是第一页
        PageRequest pageable = PageRequest.of(page - 1, limit);
        // 创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 进行模糊查询
                .withIgnoreCase(true); // 忽略大小写
        // 转换departmentQueryVo对象 为 department对象
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department); // 将departmentQueryVo对象 复制给 department
        // Example对象
        Example<Department> example = Example.of(department, matcher); // 此处的department对象要从departmentQueryVo中转换过来
        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    // 删除科室接口
    @Override
    public void remove(String hoscode, String depcode) {
        // 先查询 根据医院编号和科室编号
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            // 调用方法删除
            departmentRepository.deleteById(department.getId()); // 根据id删除
        }
    }
}
