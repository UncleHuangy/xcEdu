package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {

    @Autowired
    private CmsConfigService service;

    @Override
    @GetMapping("/getmodel/{id}")
    public CmsConfig getmodel( @PathVariable("id") String id) {
        return service.getConfigById(id);
    }
}
