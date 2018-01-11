package com.cherylcai.homework;

import com.qcloud.cos.model.COSObjectSummary;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;

import java.io.*;

/**
 * Created by cheryl.cai on 9/1/2018.
 */
public class ReplaceLink {
    String targeturl;
    String pattern="(\\!\\[\\]\\()(.*\\..*)(\\))";
    UploadToCloud uploadToCloud;
    ListObjectsRequest listObjectsRequest;
    ObjectListing objectListing;
    public ReplaceLink(UploadToCloud uploadToCloud){
        this.uploadToCloud=uploadToCloud;
        targeturl = "https://"+uploadToCloud.getBucketName()+".cos."+uploadToCloud.getRegionName()+".myqcloud.com/";
        listObjectsRequest=uploadToCloud.getListObjectsRequest();
        listObjectsRequest.setPrefix("");
        objectListing=uploadToCloud.getCosclient().listObjects(listObjectsRequest);

    }

    public void replace(File file) {
        if(file.isFile()){
            try {
                BufferedReader br=new BufferedReader(new FileReader(file));
                String line;
                StringBuffer newcontent=new StringBuffer("");
                while((line=br.readLine())!=null){
                    if(line.matches(pattern)) {
                        String filename=line.replaceAll(pattern,"$2");
                        for(COSObjectSummary cosObjectSummary:objectListing.getObjectSummaries()){
                            if(cosObjectSummary.getKey().endsWith(filename)){
                                String key = targeturl+cosObjectSummary.getKey();
                                newcontent.append(line.replaceAll(pattern,"$1"+key+"$3"));
                            }
                        }
                    }else{
                        newcontent.append(line);
                    }
                    newcontent.append("\n");
                }
                FileWriter fileWriter = new FileWriter(file.getName());
                fileWriter.write(newcontent.toString());
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }else if(file.isDirectory()){
            File[] files=file.listFiles();
            for(File perfile:files){
                replace(perfile);
            }

        }else{
            System.out.println(file.getName()+" not exist!");
        }
    }

}
