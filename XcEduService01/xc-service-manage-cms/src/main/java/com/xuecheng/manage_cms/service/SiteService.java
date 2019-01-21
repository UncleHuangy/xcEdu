package com.xuecheng.manage_cms.service;


import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService {


    @Autowired
    CmsSiteRepository cmsSiteRepository;


    /**
     * 站点查询
     * @return
     */
    public QueryResponseResult findList(){


        //页面类型：精确匹配，页面类型包括：静态和动态，在数据库中静态用“0”表示，动态用“1”表示。


        //查询出所有的的站点信息
        List<CmsSite> sites = cmsSiteRepository.findAll();

        //将所有数据封装进入
        QueryResult<CmsSite> queryResult = new QueryResult<>();

        queryResult.setTotal(sites.size());
        queryResult.setList(sites);

        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

}
