package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常捕获类
 * 捕获抛出的所有CustomException异常
 */
@ControllerAdvice// 控制器增强
public class ExceptionCatch {

    //定义 日志属性 记录异常信息
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    //使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();


    static{
        //在这里加入一些基础的异常类型判断
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }


    @ExceptionHandler(CustomException.class)   //此注解 捕获所有的CustomException异常
    @ResponseBody //返回的信息格式为json格式
    public ResponseResult customException(CustomException c){
        //三个参数     异常信息头     异常信息内容
        LOGGER.error("catch exception : {}",c.getMessage());
        //获取 异常信息
        ResultCode resultCode = c.getResultCode();
        //将结果封装
        ResponseResult responseResult = new ResponseResult(resultCode);
        //返回异常信息
        return responseResult;
    }



    @ExceptionHandler(Exception.class)  //捕获Exception异常
    @ResponseBody
    public ResponseResult exception(Exception exception){
        //记录日志
        LOGGER.error("catch exception:{}",exception.getMessage());

        //判断 集合是否已经有数据  没有数据的情况下 向集合中添加数据
        if(EXCEPTIONS == null) {
            EXCEPTIONS = builder.build();
        }

        final ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        final ResponseResult responseResult;
        //如果返回的结果 不为空 直接将结果返回
        if (resultCode != null) {
            responseResult = new ResponseResult(resultCode);
        } else {
            //返回 99999
            responseResult = new ResponseResult(CommonCode.SERVER_ERROR);
        }

        //返回信息
        return responseResult;
    }


}
