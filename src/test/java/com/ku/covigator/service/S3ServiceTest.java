package com.ku.covigator.service;

import com.ku.covigator.config.MockS3Config;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.*;

@Import(MockS3Config.class)
@SpringBootTest
class S3ServiceTest {

    @Autowired
    private S3Mock s3Mock;
    @Autowired
    private S3Service s3Service;

    @AfterEach
    public void tearDown() {
        s3Mock.stop();
    }

    @DisplayName("s3 버킷에 이미지를 업로드한다.")
    @Test
    void uploadImage() {
        //given
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );

        //when
        String uploadedImageUrl = s3Service.uploadImage(imageFile, "profile");

        //then
        assertThat(uploadedImageUrl).contains("test-image.jpg");
    }

}