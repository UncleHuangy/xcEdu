package com.xuecheng.manage_cms.service;


import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {


    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    /**
     * 页面列表分页查询
     * @param page 当前页码
     * @param size 页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页面列表
     */
    public QueryResponseResult findList(int page,int size,QueryPageRequest queryPageRequest){
        //条件匹配器
        //页面名称模糊查询，需要自定义字符串的匹配器实现模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains())
                        .withMatcher("pageName", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值
        CmsPage cmsPage = new CmsPage();
        //页面模板 pageType
        if (StringUtils.isNoneEmpty(queryPageRequest.getPageType())){
            cmsPage.setPageType(queryPageRequest.getPageType());
        }
        //页面名称
        if (StringUtils.isNoneEmpty(queryPageRequest.getPageName())){
            cmsPage.setPageName(queryPageRequest.getPageName());
        }
        //站点ID
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //页面别名
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        if (queryPageRequest == null){
            queryPageRequest =new QueryPageRequest();
        }

        //页码
        page -= 1;
        if (page < 0 ){
            page=0;
        }
        if (size <= 0){
            size =10;
        }
        //分页对象
        Pageable pageable = PageRequest.of(page,size);
        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();

        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());

        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
    }


    /**
     * 页面添加
     * @param cmsPage
     * @return
     */
    public CmsPageResult add(CmsPage cmsPage){
        //查询页面是否已经存在
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());

        if (cmsPage1 != null){

            //return new CmsPageResult(CommonCode.FAIL,null);
            // 页面已经存在 抛出异常,页面已经存在
            //throw  new CustomException(CommonCode.FAIL);
            //使用异常封装类,简化代码 如下
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);


        }

        //如果cmspage1 不存在 将数据存储
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);


    }


    //根据ID查询 页面
    public CmsPage findById(String id) {
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        if (byId.isPresent()){
            return byId.get();
        }
        return null;
    }


    //更新页面信息
    public CmsPageResult update(String id,CmsPage cmsPage) {
        //根据id查询页面信息
        CmsPage one = this.findById(id);
        if (one != null) {
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新dataUrl
            one.setDataUrl(cmsPage.getDataUrl());

            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if (save != null) {
                //返回成功
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        //返回失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }


    //删除页面
    public ResponseResult delete(String id){
        CmsPage one = this.findById(id);
        if(one!=null){
            //删除页面
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }




    //页面静态化
    public String getPageHtml(String pageId){

        //获取页面模型数据
        Map model = this.getModelByPageId(pageId);
        if(model == null){
            //获取页面模型数据为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面模板
        String templateContent = getTemplateByPageId(pageId);
        if(StringUtils.isEmpty(templateContent)){
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = generateHtml(templateContent, model);
        if(StringUtils.isEmpty(html)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;

    }
    //页面静态化
    public String generateHtml(String template,Map model){
        try {
            //生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            //模板加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template",template);
            //配置模板加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            //获取模板
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    //获取页面模板
    public String getTemplateByPageId(String pageId){
        //查询页面信息
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //页面模板
        String templateId = cmsPage.getTemplateId();
        if(StringUtils.isEmpty(templateId)){
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if(optional.isPresent()){
            CmsTemplate cmsTemplate = optional.get();
            //模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();

            System.out.println("模板文件ID:"+templateFileId);

            //取出模板文件内容
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            if (gridFSFile == null){

                System.out.println("mongodb查询出的文件模板为空");

                ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
            }

            //打开下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
    //获取页面模型数据
    public Map getModelByPageId(String pageId){
        //查询页面信息
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //取出dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;

    }


    /**
     * 发布页面
     * @param pageId
     * @return
     */
    public ResponseResult postPage(String pageId) {
        //执行静态化
        String pageHtml = this.getPageHtml(pageId);
        if (pageHtml==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }

        //保存静态化文件
        CmsPage cmsPage = saveHtml(pageId, pageHtml);

        //发送消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 保存生成的静态化文件
     *
     * @param pageId
     * @param pageHtml
     * @return
     */
    private CmsPage saveHtml(String pageId, String pageHtml) {
        //查询页面
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        CmsPage cmsPage = optional.get();

        //存储之前将之前的删除
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isNoneEmpty(htmlFileId)){
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }

        //保存html 文件到gridFS
        InputStream inputStream = IOUtils.toInputStream(pageHtml);

        ObjectId id = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        //将文件ID存储到 cmsPage中
        cmsPage.setHtmlFileId(id.toString());

        cmsPageRepository.save(cmsPage);

        return cmsPage;
    }

    /**
     * 发送消息
     * @param pageId
     */
    private void sendPostPage(String pageId) {
        //查询页面信息
        CmsPage cmsPage = this.findById(pageId);

        if (cmsPage == null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        //生成发送的 Json数据
        Map<String,String> map = new HashMap<>();
        map.put("pageId",pageId);

        //转换数据
        String s = JSON.toJSONString(map);
        //获取站点ID 作为  key
        String siteId = cmsPage.getSiteId();

        System.out.println(siteId);

        //发送消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,s);

    }
}
