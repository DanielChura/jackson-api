package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadFile(file, "products");
        if (url == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(url);
    }
}
