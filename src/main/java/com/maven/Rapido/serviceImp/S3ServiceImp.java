package com.maven.Rapido.serviceImp;

import com.maven.Rapido.payload.request.feed.CreateFeedDTO;
import com.maven.Rapido.payload.request.file.PresignedUrlRequest;
import com.maven.Rapido.payload.response.file.PresignedUrlResponse;
import com.maven.Rapido.service.S3Services;
import com.maven.Rapido.utils.UploadStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImp implements S3Services {
    private final S3Presigner s3Presigner;
    private final UploadStore uploadStore;

    @Value("${storage.s3.bucket-name}")
    private String bucketName;

    @Override
    public PresignedUrlResponse saveFileToBucket(PresignedUrlRequest request) {
        String contentType = request.getContentType();
        long fileSize = request.getFileSize();

        if (fileSize > 20 * 1024 * 1024) {
            throw new IllegalArgumentException("File too large (max 20MB)");
        }

        String extension = uploadStore.getFileExtension(contentType);
        String key = "temp/" + UUID.randomUUID() + "." + extension;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();
        log.info("presigner--- {}",s3Presigner);
        URL url = s3Presigner.presignPutObject(presignRequest).url();
        uploadStore.save(key);

        return new PresignedUrlResponse(key, url.toString());
    }

    @Override
    public void feedCreate(CreateFeedDTO request) {
        for (String key : request.getFileKeys()) {
            uploadStore.markUsed(key);
            uploadStore.moveFileToPermanentLocation(key);
        }
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void cleanupUnsubmittedUploads() {
        List<String> expiredKeys = uploadStore.getExpiredKeys();
        for (String key : expiredKeys) {
            log.info("Deleting expired temp file: {}", key);
            uploadStore.deleteFile(key);
        }
    }
}
