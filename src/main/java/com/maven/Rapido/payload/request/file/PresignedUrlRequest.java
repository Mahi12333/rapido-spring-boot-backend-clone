package com.maven.Rapido.payload.request.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PresignedUrlRequest {
    private String contentType;
    private long fileSize;
}
