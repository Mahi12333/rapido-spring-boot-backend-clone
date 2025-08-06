package com.maven.Rapido.payload.request.file;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UploadMeta {
    private String key;
    private Instant createdAt;
    private boolean used;
}
