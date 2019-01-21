package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

import javax.xml.transform.Result;

/**
 * 异常抛出类,用以封装抛出的异常
 * 简化代码
 */
public class ExceptionCast {

    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }


}
