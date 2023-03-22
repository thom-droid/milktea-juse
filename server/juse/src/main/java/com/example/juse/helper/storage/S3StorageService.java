package com.example.juse.helper.storage;

import com.example.juse.helper.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.ByteBuffer;

@Slf4j
@Service
public class S3StorageService extends AbstractStorageServiceImpl {

    private static final String S3_IMAGE_BUCKET = "chicken-milktea-juse.com";
    private static final String KEY_PREFIX = "icons/user/";
    private final S3Client s3Client;

    public S3StorageService(Tika tika, S3Client s3Client) {
        super(tika);
        this.s3Client = s3Client;
    }

    public String store(MultipartFile file) throws IOException, S3Exception {

        byte[] data = file.getBytes();
        String fileName = StringUtils.createUniqueAndRegulatedFileName(file.getOriginalFilename());
        String key = KEY_PREFIX + fileName;

        validateImageExtension(file);

        // multipart request and response id
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(S3_IMAGE_BUCKET)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .key(key)
                .build();

        CreateMultipartUploadResponse uploadResponse = s3Client.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = uploadResponse.uploadId();

        log.info("upload ID: {}", uploadId);

        // upload multipart.
        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .uploadId(uploadId)
                .key(key)
                .bucket(S3_IMAGE_BUCKET)
                .partNumber(1)
                .build();

        String etag = s3Client.uploadPart(uploadPartRequest, RequestBody.fromByteBuffer(toByteBuffer(data))).eTag();
        etag = etag.replaceAll("\"", "");

        CompletedPart part = CompletedPart.builder().partNumber(1).eTag(etag).build();

        // call MultipartUploadOperation to tell S3 to merge all uploaded parts and finish the multipart operation
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(part)
                .build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(S3_IMAGE_BUCKET)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(completedMultipartUpload)
                .build();

        s3Client.completeMultipartUpload(completeMultipartUploadRequest);

        return StringUtils.buildLocation(S3_IMAGE_BUCKET, fileName, "icons", "user");

    }

    private ByteBuffer toByteBuffer(byte[] bytes) {
        return ByteBuffer.wrap(bytes);
    }

}
