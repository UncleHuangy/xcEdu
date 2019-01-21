package com.xuecheng.cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

//定义dao                                             指定映射类类型  和  主键类型
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
}
