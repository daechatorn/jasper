package com.example.jasper.controller;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

@RestController
public class JasperController {

    @PostMapping(value = "/jasper/generate")
    public boolean generate() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CUST_NAME",                      "เดชาธร");
        hashMap.put("CUST_LASTNAME",                  "น้ำใจ");
        hashMap.put("CUST_AGE",                       25);

        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new FileInputStream("src/main/resources/templates/myReport.jasper"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hashMap, new JREmptyDataSource());
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
