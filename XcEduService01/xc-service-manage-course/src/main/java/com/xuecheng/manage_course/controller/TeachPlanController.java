package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class TeachPlanController implements CourseControllerApi {

    @Autowired
    private CourseService service;


    //根据课程id查询课程详细信息
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return service.findTeachplanList(courseId);
    }

    //添加课程计划
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return service.addTeachplan(teachplan);
    }

    //查询课程列表
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList( @PathVariable("page") int page,@PathVariable("size") int size, CourseListRequest courseListRequest) {
        return service.findCourseList(page,size,courseListRequest);
    }

    //新增课程
    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return service.addCourseBase(courseBase);
    }

    //查询课程基本信息
    @Override
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) throws RuntimeException {
        return service.getCourseBaseById(courseId);
    }

    //修改课程基本信息
    @Override
    @PostMapping("/coursebase/update")
    public ResponseResult updateCourseBase(@RequestBody CourseBase courseBase) {
        return service.updateCourseBase(courseBase);
    }

    @Override
    @GetMapping("/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        System.out.println(courseId);
        return service.getCourseMarketById(courseId);
    }

    @Override
    @PostMapping("/coursemarket/update")
    public ResponseResult updateCourseMarket(@RequestBody CourseMarket courseMarket) {
        CourseMarket courseMarket1 = service.updateCourseMarket(courseMarket);
        if (courseMarket1 == null){
            return new ResponseResult(CommonCode.FAIL);
        }else {
            return new ResponseResult(CommonCode.SUCCESS);
        }
    }


}
