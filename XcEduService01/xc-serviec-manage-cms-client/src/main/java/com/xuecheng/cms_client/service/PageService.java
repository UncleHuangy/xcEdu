package com.xuecheng.cms_client.service;


import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.cms_client.dao.CmsPageRepository;
import com.xuecheng.cms_client.dao.CmsSiteRepository;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    private CmsPageRepository pageRepository;

    @Autowired
    private CmsSiteRepository siteRepository;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    //将页面html 保存到电脑物理路径
    public void savePageToServerPath(String pageId) {
        //查询 页面信息
        Optional<CmsPage> byId = pageRepository.findById(pageId);
        //判断页面是否为空
        if (!byId.isPresent()) {
            //自定义异常页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //获取页面
        CmsPage cmsPage = byId.get();
        //得到站点
        CmsSite cmsSite = this.getCmsSiteById(cmsPage.getSiteId());
        //获取页面物理路径
        String pagePath = cmsSite.getSitePhysicalPath() + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();

        //测试输出物理路径
        System.out.println("物理路径 pagePath:" + pagePath);

        //插叙静态页面 文件
        InputStream inputStream = this.getFileById(cmsPage.getHtmlFileId());

        if(inputStream == null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        FileOutputStream fileOutputStream = null;

        try {
            //得到 输出流  见过去的文件输出到 物理路径
            fileOutputStream = new FileOutputStream(new File(pagePath));

            IOUtils.copy(inputStream,fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 通过 htmlId 找到保存的文件信息
     * 通过工具类得到文件的下载流
     *
     * @param htmlId
     * @return
     */
    public InputStream getFileById(String htmlId) {
        try {
            //通过ID查询文件
            GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlId)));

            //通过下载流操作类 得到下载流
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getObjectId());
            GridFsResource gridFsResource = new GridFsResource(file, gridFSDownloadStream);

            return gridFsResource.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


    /**
     * 获取站点信息
     *
     * @param siteId
     * @return
     */
    public CmsSite getCmsSiteById(String siteId) {
        Optional<CmsSite> byId = siteRepository.findById(siteId);
        if (byId.isPresent()) {
            CmsSite cmsSite = byId.get();
            return cmsSite;
        }
        return null;
    }


}
