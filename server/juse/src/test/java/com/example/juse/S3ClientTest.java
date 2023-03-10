package com.example.juse;

import com.example.juse.helper.storage.config.StorageConfig;
import com.example.juse.user.service.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {StorageConfig.class})
@SpringBootTest
public class S3ClientTest {

    private S3StorageService storageService;

    @MockBean
    private S3Client s3Client;

    @BeforeEach
    public void setup() {
        storageService = new S3StorageService(s3Client);
    }

    @Test
    public void givenMockedMultipart_whenUploaded_thenSucceed() throws IOException {

        //given
        byte[] image = new byte[]{1, 2, 3, 4};
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test.jpg", "test.jpg","image/jpg", image);

        //when
        Mockito.when(s3Client.createMultipartUpload(Mockito.any(CreateMultipartUploadRequest.class)))
                .thenReturn(CreateMultipartUploadResponse.builder().uploadId("123").build().toBuilder().build());

        Mockito.when(s3Client.uploadPart(Mockito.any(UploadPartRequest.class), Mockito.any(RequestBody.class)))
                .thenReturn(UploadPartResponse.builder().eTag("etag").build().toBuilder().build());

        Mockito.when(s3Client.completeMultipartUpload(Mockito.any(CompleteMultipartUploadRequest.class)))
                .thenReturn(CompleteMultipartUploadResponse.builder().key("test.jpg").build().toBuilder().build());

        //then
        storageService.store(mockMultipartFile);

        Mockito.verify(s3Client).createMultipartUpload(Mockito.any(CreateMultipartUploadRequest.class));
        Mockito.verify(s3Client).uploadPart(Mockito.any(UploadPartRequest.class), Mockito.any(RequestBody.class));
        Mockito.verify(s3Client).completeMultipartUpload(Mockito.any(CompleteMultipartUploadRequest.class));

    }

}
