package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {


    @Test
    public void testUpload(){
        try {
            //1.加载配置信息
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout="+ClientGlobal.g_network_timeout+"ms");
            System.out.println("charset="+ClientGlobal.g_charset);

            //2.创建客户端
            TrackerClient tc = new TrackerClient();
            //3.连接 tracker service
            TrackerServer connection = tc.getConnection();
            if (connection == null){
                System.out.println("getConnection null");
                return;
            }
            //4. 获取一个storage server  通过tracker server
            StorageServer storeStorage = tc.getStoreStorage(connection);
            if (storeStorage == null){
                System.out.println("getStoreStorage return null");
                return;
            }
            //5.创建一个storage 存储客户端
            StorageClient1 sc1 = new StorageClient1(connection,storeStorage);

            NameValuePair[] meta_list = null;
            String item = "D:\\英雄时刻\\19748847\\323482.jpg";

            String fileId;

            //上传文件
            fileId = sc1.upload_file1(item, "jpg", meta_list);

            System.out.println(item);//group1/M00/00/01/wKgZmVxHvEKAbK46AAEcd48S58k020.jpg

            System.out.println(fileId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }


    //下载文件
    @Test
    public void testDown(){
        try {
            //1.加载配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //2.定义trackerClient,用于请求trackerServer
            TrackerClient trackerClient = new TrackerClient();
            //3.连接tracker
            TrackerServer connection = trackerClient.getConnection();
            //4.获取tracker
            StorageServer storageServer = null;
            //5.创建StroageClient1            参数:
            StorageClient1 client1 = new StorageClient1(connection,storageServer);

            //6.文件下载
            byte[] bytes = client1.download_file1("group1/M00/00/01/wKgZmVxHvEKAbK46AAEcd48S58k020.jpg");
            //7.输出文件
            File file = new File("D:\\英雄时刻\\323482.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }


    //查询文件
    @Test
    public void testQueryFile() throws IOException, MyException {
        //1.加载配置
        ClientGlobal.initByProperties("config/fastdfs-client.properties");
        //2.定义trackerClient,用于请求trackerServer
        TrackerClient trackerClient = new TrackerClient();
        //3.连接tracker
        TrackerServer connection = trackerClient.getConnection();
        //4.获取tracker
        StorageServer storageServer = null;
        //5.创建storageServer
        StorageClient storageClient = new StorageClient(connection,storageServer);

        //6.查询
        FileInfo group1 = storageClient.query_file_info("group1", "M00/00/01/wKgZmVxHvEKAbK46AAEcd48S58k020.jpg");


        System.out.println(group1);


    }




    //上传文件
    @Test
    public void testUpload1(){

        try {
            //1.加载fastdfs-client.properties配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //2.定义TrackerClient，用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //3.连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //4.获取Stroage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //5.创建stroageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //6.向stroage服务器上传文件
            //本地文件的路径
            String filePath = "d:/logo.png";
            //7.上传成功后拿到文件Id
            String fileId = storageClient1.upload_file1(filePath, "png", null);
            System.out.println(fileId);
            //group1/M00/00/01/wKhlQVuhU3eADb4pAAAawU0ID2Q159.png

        } catch (Exception e) {
            e.printStackTrace();
        }

    }





}
