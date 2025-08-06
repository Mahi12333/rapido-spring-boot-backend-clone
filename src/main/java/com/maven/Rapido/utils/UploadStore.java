package com.maven.Rapido.utils;

import com.maven.Rapido.payload.request.file.UploadMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class UploadStore {
    private final S3Client s3Client;

    @Value("${storage.s3.bucket-name}")
    private String bucketName;

    private final Map<String, UploadMeta> uploads = new ConcurrentHashMap<>();
    public void save(String key) {
        uploads.put(key, new UploadMeta(key, Instant.now(), false));
    }
    public void markUsed(String key) {
        UploadMeta meta = uploads.get(key);
        if (meta != null) {
            meta.setUsed(true);
        }
    }
    public List<String> getExpiredKeys() {
        List<String> expired = new ArrayList<>();
        Instant now = Instant.now();
        uploads.forEach((key, meta) -> {
            if (!meta.isUsed() && now.isAfter(meta.getCreatedAt().plus(Duration.ofMinutes(15)))) {
                expired.add(key);
            }
        });
        return expired;
    }

    public void remove(String key) {
        uploads.remove(key);
    }


    @Retryable(retryFor = { S3Exception.class, IOException.class }, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public void moveFileToPermanentLocation(String tempKey) {
        String categoryFolder = getCategoryFolder(tempKey);
        String newKey = tempKey.replace("temp/", categoryFolder);

        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(tempKey)
                .destinationBucket(bucketName)
                .destinationKey(newKey)
                .build();
        s3Client.copyObject(copyRequest);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(tempKey)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    public String getCategoryFolder(String key) {
        if (key.endsWith(".jpg") || key.endsWith(".png")) return "uploads/image/";
        if (key.endsWith(".mp4")) return "uploads/video/";
        if (key.endsWith(".csv")) return "uploads/csv/";
        return "uploads/others/";
    }

    public String getFileExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "video/mp4" -> "mp4";
            case "text/csv" -> "csv";
            default -> "bin";
        };
    }

    @Retryable(retryFor = { S3Exception.class, IOException.class }, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public void deleteFile(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
        remove(key); // also remove from in-memory
    }


}
