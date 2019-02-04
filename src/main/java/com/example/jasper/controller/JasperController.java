package com.example.jasper.controller;

import com.example.jasper.model.ItemJR;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class JasperController {

    @PostMapping(value = "/jasper/generate")
    public boolean generate() {

        List<ItemJR> itemJRList = new ArrayList();
        for(int i = 1 ; i <100 ;i++){
            ItemJR itemJR = new ItemJR()
                    .setItemName("มือถือ"+i)
                    .setItemPrice(BigDecimal.valueOf(10000.00).add(BigDecimal.valueOf(i)));
            itemJRList.add(itemJR);
        }

        JRBeanCollectionDataSource itemDataSource = new JRBeanCollectionDataSource(itemJRList);

        HashMap<String, Object> paramaters = new HashMap<>();
        paramaters.put("CUST_NAME",                      "เดชาธร");
        paramaters.put("CUST_LASTNAME",                  "น้ำใจ");
        paramaters.put("CUST_AGE",                       25);
        paramaters.put("ItemDataSource",                 itemDataSource);


        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new FileInputStream("src/main/resources/templates/myReport.jasper"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, paramaters, new JREmptyDataSource());
            byte[] source = JasperExportManager.exportReportToPdf(jasperPrint);
            InputStream myInputStream = new ByteArrayInputStream(source);

            uploadFileToMinio(myInputStream);

        }catch (JRException e){
            System.out.println("Cannot compile jasper file");
        } catch (FileNotFoundException e){
            System.out.println("Jasper file path is in correct");
        }

        return true;

    }

    public void uploadFileToMinio(InputStream myInputStream){
        try {
            // Create a minioClient with the Minio Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("https://play.minio.io:9000", "Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");

            // Check if the bucket already exists.
            boolean isExist = minioClient.bucketExists("daechatornman");
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // Make a new bucket called asiatrip to hold a zip file of photos.
                minioClient.makeBucket("daechatornman");
            }
            minioClient.putObject("daechatornman","docuementFileByte1.pdf", myInputStream, "application/json");
            System.out.println("File is successfully uploaded as docuementFileByte1.pdf to `daechatornman` bucket.");
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
