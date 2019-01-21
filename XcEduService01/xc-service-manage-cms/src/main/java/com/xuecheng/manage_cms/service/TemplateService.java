package com.xuecheng.manage_cms.service;


import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {


    @Autowired
    CmsTemplateRepository cmsTemplateRepository;


    /**
     * 站点查询
     * @return
     */
    public QueryResponseResult findList(){

        //查询出所有的的站点信息
        List<CmsTemplate> templates = cmsTemplateRepository.findAll();

        //将所有数据封装进入
        QueryResult<CmsTemplate> queryResult = new QueryResult<>();

        queryResult.setTotal(templates.size());
        queryResult.setList(templates);

        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

}
