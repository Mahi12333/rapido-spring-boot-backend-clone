package com.maven.Rapido.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String userUploads, int maxFileSize) throws IOException;
}
