package com.xuecheng.manage_cms.test;


import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GridFSTestUp {

    @Autowired
    public GridFsTemplate gridFsTemplate;

    @Autowired
    public GridFSBucket gridFSBucket;


    @Test
    public void testGridFsDown() throws IOException {
        String id="5c43f8fd90ecb42fb4f61128";

        //根据ID查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));

        //打开下载流对象
        GridFSDownloadStream stream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        //创建gridFSresource,用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,stream);

        //获取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");

        System.out.println(s);


    }



    /**
     * 上传文件
     * @throws FileNotFoundException
     */
    @Test
    public void testUp() throws FileNotFoundException {
        //定义要上传的文件
        File file = new File("D:\\a/index_banner.html");
        //定义输入流
        FileInputStream inputStream = new FileInputStream(file);

        //上传文件
        ObjectId objectId = gridFsTemplate.store(inputStream, "轮播图测试文件");

        System.out.println(objectId);
    }


    //删除文件
    @Test
    public void testDelFile() throws IOException {
        //根据文件id删除fs.files和fs.chunks中的记录
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5b32480ed3a022164c4d2f92")));
    }


}
