package com.ku.covigator.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ku.covigator.config.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@EnableConfigurationProperties(S3Properties.class)
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client s3Client;
    private final S3Properties s3Properties;
    private static final String PROFILE_IMAGE_BASE_DIRECTORY = "profile-image/";

    public String uploadImage(MultipartFile multipartFile) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        String fileName = createFileName(multipartFile.getOriginalFilename());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(
                    new PutObjectRequest(s3Properties.getBucket(), fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            log.error("S3 파일 업로드에 실패했습니다. {}", e.getMessage());
        }

        return s3Client.getUrl(s3Properties.getBucket(), fileName).toString();
    }

//    public void deleteImage(String fileName) {
//        s3Client.deleteObject(
//                new DeleteObjectRequest(s3Properties.getBucket(), fileName)
//        );
//    }

    private String createFileName(String fileName) {

        String uniqueID = '$' + UUID.randomUUID().toString();
        return PROFILE_IMAGE_BASE_DIRECTORY + fileName.concat(uniqueID);
    }

}
