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

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by cheryl.cai on 7/1/2018.
 */
public class UploadToCloud {
    private COSCredentials cred;
    private ClientConfig clientConfig;
    private COSClient cosclient;
    private String regionName;
    private String bucketName;
    private PutObjectRequest putObjectRequest;
    private PutObjectResult putObjectResult;
    private ListObjectsRequest listObjectsRequest;
    private ObjectListing objectListing;

    public boolean forceReplace;
    public boolean imgOnly;
    public List<String> sourcePaths;
    public String destination;

    public String getBucketName(){
        return bucketName;
    }
    public String getRegionName(){
        return regionName;
    }
    public ListObjectsRequest getListObjectsRequest(){
        return listObjectsRequest;
    }
    public COSClient getCosclient(){
        return cosclient;
    }

    private  List<String> imgSuffix=new ArrayList<>(Arrays.asList("png","jpg","gif"));

    public UploadToCloud(){
        // 1 初始化用户身份信息(secretId, secretKey)
        cred = new BasicCOSCredentials("AKIDd5HEwQTfQwDF3dcRVLwIQKzQceR0mRIv", "XprMcmgLktnRpyrWx93i7G9m9HApGSQ9");
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        regionName="ap-beijing";
        clientConfig = new ClientConfig(new Region(regionName));
        // 3 生成cos客户端
        cosclient = new COSClient(cred, clientConfig);
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        bucketName = "test-1255845291";

        listObjectsRequest=new ListObjectsRequest(bucketName,"","/","",100);

        forceReplace=false;
        imgOnly=false;
        sourcePaths=new ArrayList<>();
        destination="/test-"+ LocalDate.now()+"/";

    }
    public void uploadFile() {
        if(sourcePaths.size()<1){
            sourcePaths=Arrays.asList(new File(".").list());
        }
        if(destination.charAt(destination.length()-1)!='/'){
            destination=destination+'/';
        }
        if(!imgOnly){
            for(String path:sourcePaths) {
                File file = new File(path);
                uploadGeneralFileAction(file, destination);
            }
        }else{
            for(String path:sourcePaths) {
                File file = new File(path);
                uploadCertainFileAction(file, destination);
            }
        }
        cosclient.shutdown();

    }

    public void uploadGeneralFileAction(File file, String address){
        if(file.exists()&&file.isFile()){
            uploadFileAction(file,address);
        }else if(file.exists()&&file.isDirectory()){
            File[] files = file.listFiles();
            for(File perFile:files){
                uploadGeneralFileAction(perFile,address);
            }
        }
    }

    public void uploadFileAction(File file, String address){
        if (forceReplace) {
            putObjectRequest = new PutObjectRequest(bucketName, address + file.getPath(), file);
            putObjectResult = cosclient.putObject(putObjectRequest);
            System.out.println("upload file " + file.getPath() + " mission succeeded!");
        } else {
            if (!checkExistingFile(address + file.getPath())) {
                putObjectRequest = new PutObjectRequest(bucketName, address + file.getPath(), file);
                putObjectResult = cosclient.putObject(putObjectRequest);
                System.out.println("upload file " + file.getPath() + " mission succeeded!");
            }
        }
    }

    public void uploadCertainFileAction(File file,String address){

        if(file.exists()&&file.isFile()){
            if(imgSuffix!=null && imgSuffix.contains(file.getName().substring(file.getName().indexOf(".")+1))){
                uploadFileAction(file,address);
            }
        }else if(file.exists()&&file.isDirectory()){
            File[] files = file.listFiles();
            for(File perFile:files){
                uploadCertainFileAction(perFile,address);
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
