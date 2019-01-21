package com.xuecheng.cms_client;

import com.xuecheng.cms_client.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class test {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public  void test(){
        cmsPageRepository.findAll();
    }
}
