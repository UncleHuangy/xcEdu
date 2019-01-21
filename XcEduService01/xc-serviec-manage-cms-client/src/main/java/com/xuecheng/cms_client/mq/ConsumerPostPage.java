package com.xuecheng.cms_client.mq;


import com.alibaba.fastjson.JSON;
import com.xuecheng.cms_client.dao.CmsPageRepository;
import com.xuecheng.cms_client.service.PageService;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConsumerPostPage {


    @Autowired
    PageService pageService;


    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg){
        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        //得到消息中的页面id
        String pageId = (String) map.get("pageId");

        //调用service方法将页面从GridFs中下载到服务器
        pageService.savePageToServerPath(pageId);

    }


}
