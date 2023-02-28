package com.dengzh.wyy.common.exception;

import com.dengzh.wyy.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义全局异常类
 *
 * @author dengzh
 */
@Data
@ApiModel(value = "自定义全局异常类")
public class wyyException extends RuntimeException {

    @ApiModelProperty(value = "异常状态码")
    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public wyyException(String message, Integer code) {
        super(message);// super 调用父类：RuntimeException
        this.code = code;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public wyyException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "wyyException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
