package com.dengzh.wyy.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dengzh.wyy.hosp.repository.DepartmentRepository;
import com.dengzh.wyy.hosp.service.DepartmentService;
import com.dengzh.wyy.model.hosp.Department;
import com.dengzh.wyy.model.vo.hosp.DepartmentQueryVo;
import com.dengzh.wyy.model.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // 根据医院编号，查询所有科室的列表  由于要在前端树形显示科室信息，我们要封装好数据
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        /*封装数据逻辑：
         * 1. 根据医院的编号，查询是所有的科室的列表 Department
         * 2. 从所有的科室的信息中，获取大科室编号以及其对应的所有数据
         * 3. 从大科室编号中进行分组，获取每个大科室下面的子科室
         * 4. 封装大科室信息，编号+大科室名字
         * 5. 封装小科室信息，科室编号+科室名字
         * 6. 把小科室的数据放到大科室的children里面
         * 7. 把大科室数据放到最终的结果集合里面
         * */
        // list 集合用于最终的数据的封装
        List<DepartmentVo> result = new ArrayList<>();
        // mongodb中根据编号查询所有科室的列表
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);// 用一个对象去查询科室的信息，对象中只包含医院的编号，
        // 1 得到所有科室的列表
        List<Department> deptList = departmentRepository.findAll(example);

        // 使用java8的流操作 2 获得大科室以及对应的所有数据（包含子科室、名称等等）
        // 3 根据大科室编号进行分组，获取每个大科室里面的下级子科室
        Map<String, List<Department>> departmentMap = deptList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        // 遍历map结合
        for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet()) {
            //4. 封装大科室数据
            String bigCode = entry.getKey(); // 大科室的编号  即：键
            List<Department> departmentList = entry.getValue(); // 大科室编号对应的全局list数据 即：值
            DepartmentVo bigDepartment = new DepartmentVo(); // 大科室对象
            bigDepartment.setDepcode(bigCode); // 键 就是大科室编号
            bigDepartment.setDepname(departmentList.get(0).getBigname()); // 从值中获取大科室的名字 要观察数据的结构 值->第一个->获取名字
            // 这里还差一个children字段，（是一个list集合）没有封装，下面将封装好的小科室数据放到children中来

            // 5. 封装小科室数据 原来的数据是一个list ， 即：大科室编号对应的全局list数据
            List<DepartmentVo> children = new ArrayList<>(); //定义要返回封装好的集合
            for (Department department : departmentList) {
                DepartmentVo smallDepartment = new DepartmentVo(); //小科室对象
                smallDepartment.setDepcode(department.getDepcode()); // 小科室编号
                smallDepartment.setDepname(department.getDepname()); // 小科室名字
                // 封装到list集合
                children.add(smallDepartment);
            }
            // 把小科室的数据封装到大科室的children中
            bigDepartment.setChildren(children);
            // 封装到最终结果
            result.add(bigDepartment);
        }
        return result;
    }

    // 根据医院编号和科室编号获取科室名称
    @Override
    public String getDepartmentName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            return department.getDepname();
        }
        return null;
    }
}
























