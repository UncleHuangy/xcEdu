package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="配置管理课程",description = "课程配置管理接口，提供数据模型的管理、查询课程")
public interface CourseControllerApi {

    @ApiOperation("查询课程计划")
    public TeachplanNode findTeachplanList(String courseId);


    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(Teachplan teachplan);



    //查询课程列表
    @ApiOperation("查询我的课程列表")
    public QueryResponseResult findCourseList(
            int page,
            int size,
            CourseListRequest courseListRequest
    );


}