package com.maven.Rapido.payload.response.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PresignedUrlResponse {
    private String fileKey;
    private String url;
}
