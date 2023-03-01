package com.dengzh.wyy.hosp.controller.api;

import com.dengzh.wyy.common.exception.wyyException;
import com.dengzh.wyy.common.helper.HttpRequestHelper;
import com.dengzh.wyy.common.result.Result;
import com.dengzh.wyy.common.result.ResultCodeEnum;
import com.dengzh.wyy.common.utils.MD5;
import com.dengzh.wyy.hosp.service.HospitalService;
import com.dengzh.wyy.hosp.service.HospitalSetService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    // 上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        // 获取传来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 1.获取医院模拟系统传来的签名 该签名已加密
        String hospSign = (String) paramMap.get("sign");

        // 2.根据传过来的医院编码，查询数据库得到hospitalSet对象，再根据该对象获得签名
        String hoscode = (String) paramMap.get("hoscode");

        // 调用HospitalSetService的方法查询数据库
        String signKey = hospitalSetService.getSignKey(hoscode);
        // 4. 签名比对 先给查出来的签名加密
        String encrypt = MD5.encrypt(signKey);

        // 判断是否一致，不一致抛出异常
        if (!hospSign.equals(encrypt)) {
            throw new wyyException(ResultCodeEnum.SIGN_ERROR); // 用枚举类 签名错误
        }

        //签名一致再调用service的方法
        hospitalService.save(paramMap);
        return Result.ok();
    }
}
