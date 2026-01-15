package com.maven.Rapido.controller;


import com.maven.Rapido.payload.request.feed.CreateFeedDTO;
import com.maven.Rapido.payload.request.file.PresignedUrlRequest;
import com.maven.Rapido.payload.response.file.PresignedUrlResponse;
import com.maven.Rapido.service.S3Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/feed")
public class S3BucketController {

     private final S3Services s3Services;
     // file upload throgh presigned URL
    @PostMapping("/presign-file-upload")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(@RequestBody PresignedUrlRequest request) {
        PresignedUrlResponse response = s3Services.saveFileToBucket(request);
        return ResponseEntity.ok(response);
    }
    // file upload through s3 client



    @PostMapping("/submit")
    public ResponseEntity<Void> submitForm(@RequestBody CreateFeedDTO request) {
        s3Services.feedCreate(request);
        return ResponseEntity.ok().build();
    }
}
