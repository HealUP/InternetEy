package com.dengzh.wyy.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengzh.wyy.common.exception.wyyException;
import com.dengzh.wyy.common.result.Result;
import com.dengzh.wyy.common.utils.MD5;
import com.dengzh.wyy.hosp.service.HospitalSetService;
import com.dengzh.wyy.model.hosp.HospitalSet;
import com.dengzh.wyy.model.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.functors.TruePredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin//允许跨域访问
public class HospitalSetController {
    //注入service
    @Autowired
    private HospitalSetService hospitalSetService;

    //1 查询医院设置表的所有信息
    @ApiOperation(value = "获取所有医院设置信息")
    @GetMapping("findAll")
    public Result findAllHospitalSet() {
        //调用service的方法 list()
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    @ApiOperation(value = "逻辑删除医院设置信息")
    // 2.逻辑删除
    @DeleteMapping("{id}")
    public Result removeHospSet(
            @ApiParam(name = "id", value = "医院设置id", required = true)
            @PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation(value = "指定信息分页查询医院设置信息")
    //3.条件查询带分页
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(
            @ApiParam(name = "current", value = "当前页码", required = true)
            @PathVariable long current,
            @ApiParam(name = "limit", value = "页面大小", required = true)
            @PathVariable long limit,
            @ApiParam(name = "hospitalSetQueryVo", value = "查询条件")
            @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) { //要求前端传递json格式数据，非必须
        //创建Page对象，传递当前页，每一页的大小
        Page<HospitalSet> page = new Page<>(current, limit);
        //使用条件构造器构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        //检查是否传相关参数
        String hoscode = hospitalSetQueryVo.getHoscode();
        String hosname = hospitalSetQueryVo.getHosname();

        if (!StringUtils.isEmpty(hoscode)) {
            wrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }
        if (!StringUtils.isEmpty(hosname)) {
            wrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        //调用方法实现分页查询
        IPage<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper);

        //返回结果
        return Result.ok(pageHospitalSet);
    }

    //4. 添加医院设置信息
    @ApiOperation(value = "添加医院设置信息")
    @PostMapping("saveHospitalSet")
    public Result savaHospitalSet(
            @ApiParam(name = "hospitalSet", value = "医院信息", required = true)
            @RequestBody HospitalSet hospitalSet) {
        // 后台设置默认使用状态  1 使用 0 不能使用
        hospitalSet.setStatus(1);
        // 后台设置签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));//md5加密系统时间+0~1000的任意数字
        //调用service
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //5 根据id获取医院设置信息
    @ApiOperation(value = "根据id获取医院设置信息")
    @GetMapping("getHospitalSet/{id}")
    public Result getHospitalSet(@ApiParam(name = "id", value = "医院id", required = true)
                                 @PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);;

        if (hospitalSet != null) {
            return Result.ok(hospitalSet);
        } else {
            return Result.fail();
        }
    }

    //6 修改医院设置信息
    @ApiOperation(value = "修改医院设置信息")
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@ApiParam(name = "hospitalSet", value = "修改对象的信息", required = true)
                                    @RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //7 批量删除医院设置信息
    @ApiOperation(value = "批量删除医院设置信息")
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@ApiParam(name = "idList", value = "包含id的list", required = true)
                                         @RequestBody List<Long> idList) {
        boolean flag = hospitalSetService.removeByIds(idList);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //8.医院设置锁定和解锁
    @ApiOperation(value = "医院设置锁定和解锁")
    @PutMapping("lockHsopitalSet/{id}/{status}")
    public Result lockHsopitalSet(@ApiParam(name = "id", value = "医院id", required = true)
                                  @PathVariable Long id,
                                  @ApiParam(name = "status", value = "医院状态",required = true)
                                  @PathVariable Integer status) {
        //根据id找到该医院  这里要做判断 万一没找着呢？
        HospitalSet hospitalSet;
        try {//当id不存在时，就会抛出异常了！
            hospitalSet = hospitalSetService.getById(id);
            //设置状态
            hospitalSet.setStatus(status);
        } catch (Exception e) {
            throw new wyyException("不存在该医院！", 201);
        }
        //调用方法
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }


    //9.发送签名密钥
    @ApiOperation(value = "发送签名密钥")
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(
            @ApiParam(name = "id", value = "医院id")
            @PathVariable Long id) {
        //查找该医院
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();//获取密钥
        String hoscode = hospitalSet.getHoscode();//获取医院编号
        //TODO 发送短信
        return Result.ok();
    }

}
