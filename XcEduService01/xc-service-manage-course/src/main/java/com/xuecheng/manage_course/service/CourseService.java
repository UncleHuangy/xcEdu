package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    //课程计划
    @Autowired
    private TeachplanMapper mapper;

    //课程列表
    @Autowired
    private CourseMapper courseMapper;

    //课程基本信息
    @Autowired
    private CourseBaseRepository courseBaseRepository;

    //课程计划
    @Autowired
    private TeachplanRepository teachplanRepository;

    //课程营销计划
    @Autowired
    private CourseMarketRepository courseMarketRepository;


    //查询课程营销计划
    public CourseMarket getCourseMarketById(String courseid){
        Optional<CourseMarket> byId = courseMarketRepository.findById(courseid);
        if (!byId.isPresent()){
            return null;
        }
        return byId.get();
    }

    //添加修改课程营销计划
    public CourseMarket updateCourseMarket(CourseMarket courseMarket){

        CourseMarket save = courseMarketRepository.save(courseMarket);
        return save;
    }


    //新增课程
    public AddCourseResult addCourseBase(CourseBase courseBase){

        //课程状态默认为发布
        courseBase.setStatus("1");
        courseBaseRepository.save(courseBase);

        return new AddCourseResult(CommonCode.SUCCESS,courseBase.getId());
    }




    //查询课程计划
    public TeachplanNode findTeachplanList(String courseId) {

        TeachplanNode teachplanNode = mapper.selectList(courseId);
        System.out.println(teachplanNode);
        return teachplanNode;
    }


    //获取课程根结点，如果没有则添加根结点
    public String getTeachplanRoot(String courseId) {

        //验证课程ID
        Optional<CourseBase> byId = courseBaseRepository.findById(courseId);

        if (!byId.isPresent()) {
            return null;
        }
        //课程信息
        CourseBase courseBase = byId.get();
        //查询课程的根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");

        if (teachplanList == null || teachplanList.size() <= 0) {
            //查询不到,要自动添加根节点
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        //返回根结点id
        return teachplanList.get(0).getId();
    }

    //添加课程计划
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        //校验课程ID和课程名称
        if (teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //取出课程ID
        String courseid = teachplan.getCourseid();
        //取出父节点id
        String parentid = teachplan.getParentid();
        if (parentid == null){
            //如果父节点为空则获取根节点
            parentid = this.getTeachplanRoot(courseid);
        }

        //取出父节点信息
        Optional<Teachplan> byId = teachplanRepository.findById(parentid);
        if (!byId.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //父节点
        Teachplan teachplan1 = byId.get();
        //父节点级别
        String grade = teachplan1.getGrade();
        //设置父节点
        teachplan.setParentid(parentid);
        teachplan.setStatus("0");//未发布
        //子节点的级别,根据父节点来判断
        if(grade.equals("1")){
            teachplan.setGrade("2");
        }else if(grade.equals("2")) {
            teachplan.setGrade("3");
        }
        //设置课程id
        //设置课程id
        teachplan.setCourseid(teachplan1.getCourseid());
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);

    }


    //分页查询
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {
        //判断条件是否为空
        if (courseListRequest == null){
            courseListRequest = new CourseListRequest();
        }

        if (size <= 0 ){
            size = 10;
        }
        if (page <= 1){
            page = 1;
        }
        //分页查询
        PageHelper.startPage(page,size);

        //查询
        Page<CourseInfo> listPage = courseMapper.findCourseListPage(courseListRequest);
        //查询列表
        List<CourseInfo> result = listPage.getResult();
        //查询总记录数
        long total = listPage.getTotal();

        //查询结果集
        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setList(result);
        queryResult.setTotal(total);

        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }


    //根据课程id查询信息
    public CourseBase getCourseBaseById(String courseId) {
        Optional<CourseBase> byId = courseBaseRepository.findById(courseId);
        if (!byId.isPresent()){
            ExceptionCast.cast(CommonCode.FAIL);
        }

        return byId.get();
    }

    //修改课程基本信息
    public ResponseResult updateCourseBase(CourseBase courseBase) {
        CourseBase save = courseBaseRepository.save(courseBase);

        return new ResponseResult(CommonCode.SUCCESS);
    }
}
