package com.cherylcai.homework;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;

import java.io.File;

/**
 * Created by cheryl.cai on 7/1/2018.
 */
public class UploadToCloud {
    private COSCredentials cred;
    private ClientConfig clientConfig;
    private COSClient cosclient;
    private String bucketName;
    private PutObjectRequest putObjectRequest;
    private PutObjectResult putObjectResult;
    private ListObjectsRequest listObjectsRequest;
    private ObjectListing objectListing;
    public boolean forceReplace;

    public UploadToCloud(){
        // 1 初始化用户身份信息(secretId, secretKey)
        cred = new BasicCOSCredentials("AKIDd5HEwQTfQwDF3dcRVLwIQKzQceR0mRIv", "XprMcmgLktnRpyrWx93i7G9m9HApGSQ9");
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        clientConfig = new ClientConfig(new Region("ap-beijing"));
        // 3 生成cos客户端
        cosclient = new COSClient(cred, clientConfig);
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        bucketName = "test-1255845291";

        listObjectsRequest=new ListObjectsRequest(bucketName,"","/","",100);

        forceReplace=false;
    }

    public void uploadFile(File file, String address){
        if(file.exists()&&file.isFile()){
            if(forceReplace){
                putObjectRequest = new PutObjectRequest(bucketName, address + file.getPath(), file);
                putObjectResult = cosclient.putObject(putObjectRequest);
                System.out.println("upload file "+file.getPath()+" mission succeeded!");
            }else {
                if (!checkExistingFile(address + file.getPath())) {
                    putObjectRequest = new PutObjectRequest(bucketName, address + file.getPath(), file);
                    putObjectResult = cosclient.putObject(putObjectRequest);
                    System.out.println("upload file "+file.getPath()+" mission succeeded!");
                }
            }
        }else if(file.exists()&&file.isDirectory()){
            File[] files = file.listFiles();
            for(File perFile:files){
                uploadFile(perFile,address);
            }
        }
    }

    public boolean checkExistingFile(String name){
        listObjectsRequest.setPrefix(name);
        objectListing=cosclient.listObjects(listObjectsRequest);
        if(objectListing.getObjectSummaries().size()>0){
            System.out.println("File "+ name + " already exists, please rename your file or upload to a different location!");
            return true;
        }
        return false;
    }

}
