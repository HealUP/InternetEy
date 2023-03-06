package com.dengzh.wyy.hosp.controller.api;

import com.dengzh.wyy.common.exception.wyyException;
import com.dengzh.wyy.common.helper.HttpRequestHelper;
import com.dengzh.wyy.common.result.Result;
import com.dengzh.wyy.common.result.ResultCodeEnum;
import com.dengzh.wyy.common.utils.MD5;
import com.dengzh.wyy.hosp.service.HospitalService;
import com.dengzh.wyy.hosp.service.HospitalSetService;
import com.dengzh.wyy.model.hosp.Hospital;
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

    // 根据医院编号查询医院信息
    @PostMapping("hospital/show")
    public Result getHospotal(HttpServletRequest request) {
        // 获取传递过来的医院的信息
        Map<String, String[]> resultMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(resultMap);
        // 获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        // 1 获取医院系统传递过来的签名，已经进行了MD5加密了
        String hospSign = (String) paramMap.get("sign");
        // 2.根据医院编码，查询数据库中医院的签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        // 3 把数据库查询得到的签名进行MD5加密
        String signKeyMD5 = MD5.encrypt(signKey);

        // 4 判断签名是否一致
        if (!hospSign.equals(signKeyMD5)) {
            throw new wyyException(ResultCodeEnum.SIGN_ERROR); //签名错误
        }

        // 调用service方法 根据医院编号从mongoDB数据库中查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);

        return Result.ok(hospital); // 返回医院信息
    }

    // 上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        // 获取传来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
// 签名完善  修改医院系统中的sign的位置，直接进行MD5加密而不使用工具类加密
        // 1.获取医院模拟系统传来的签名 该签名已加密
        String hospSign = (String) paramMap.get("sign");

        // 2.根据传过来的医院编码，查询数据库得到hospitalSet对象，再根据该对象获得签名
        String hoscode = (String) paramMap.get("hoscode");

        // 调用HospitalSetService的方法查询数据库医院的签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        // 4. 签名比对 先给查出来的签名加密
        String encrypt = MD5.encrypt(signKey);

        // 判断是否一致，不一致抛出异常
        if (!hospSign.equals(encrypt)) {
            throw new wyyException(ResultCodeEnum.SIGN_ERROR); // 用枚举类 签名错误
        }

        // 处理编码问题
        // base64编码导致`传输过程中的“+” 转换成了 “ ”
        String logoData = (String) paramMap.get("logoData");
        logoData = logoData.replaceAll(" ","+"); // 换回来
        // 存回去
        paramMap.put("logoData", logoData);

        //签名一致再调用service的方法
        hospitalService.save(paramMap);
        return Result.ok();
    }
}
