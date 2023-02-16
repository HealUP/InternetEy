package com.dengzh.wyy.common.exception;

import com.dengzh.wyy.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


/**
* Description: 全局异常处理类
* date: 2023/2/16 18:31
 *
* @author: Deng
* @since JDK 1.8
*/
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     *全局异常处理
     * @param e
     * @return
     */

    @ExceptionHandler(Exception.class)//这句话的意思就是，捕获到所有的异常
    @ResponseBody//返回json格式给前端
    public Result error(Exception e) {
        e.printStackTrace();//打印异常信息
        return Result.fail();
    }

    /**
     * 自定义异常处理  使用的时候需要手动抛出
     * @param e
     * @return
     */
    @ExceptionHandler(wyyException.class)
    @ResponseBody
    public Result error(wyyException e) {
        return Result.build(e.getCode(), e.getMessage());
    }
}
