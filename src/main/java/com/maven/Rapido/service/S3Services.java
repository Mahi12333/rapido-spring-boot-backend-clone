package com.maven.Rapido.service;

import com.maven.Rapido.payload.request.feed.CreateFeedDTO;
import com.maven.Rapido.payload.request.file.PresignedUrlRequest;
import com.maven.Rapido.payload.response.file.PresignedUrlResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

public interface S3Services {
    @Retryable(retryFor = { S3Exception.class, IOException.class }, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    PresignedUrlResponse saveFileToBucket(PresignedUrlRequest request);
    void feedCreate(CreateFeedDTO request);
}
