package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {


    @Autowired
    PageService pageService;


    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable(name = "page") int page, @PathVariable(name = "size") int size, QueryPageRequest queryPageRequest) {

       /* //暂时采用测试数据,测试接口是否是可以正常运行
        QueryResult queryResult = new QueryResult();
        queryResult.setTotal(2);

        //静态数据列表
        List list = new ArrayList();

        //创建实体类
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("测试数据");

        list.add(cmsPage);

        queryResult.setList(list);

        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);

        return queryResponseResult;*/

        return pageService.findList(page,size,queryPageRequest);
    }

    /**
     * 添加页面
     * @param cmsPage
     * @return
     */
    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }

    /**
     * 根据ID查询页面信息
     * @param id
     * @return
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return pageService.findById(id);
    }

    /**
     * 更新页面
     * @param id
     * @param cmsPage
     * @return
     */
    @Override
    @PutMapping("/edit/{id}")//这里使用put方法，http 方法中put表示更新
    public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return pageService.update(id,cmsPage);
    }

    /**
     * 删除页面
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}") //使用http的delete方法完成岗位操作
    public ResponseResult delete(@PathVariable("id") String id) {
        return pageService.delete(id);
    }

    /**
     * 发布页面
     * @param pageId
     * @return
     */
    @Override
    @RequestMapping("/postPage/{pageId}")
    public ResponseResult post( @PathVariable("pageId") String pageId) {

        System.out.println("页面发布控制器");
        return pageService.postPage(pageId);
    }


}
