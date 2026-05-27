package com.school.storage;

import com.school.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final CloudinaryStorageService cloudinaryStorageService;

    public FileUploadController(CloudinaryStorageService cloudinaryStorageService) {
        this.cloudinaryStorageService = cloudinaryStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = cloudinaryStorageService.uploadFile(file);
            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        }
    }

    @PostMapping("/upload/compressed")
    public ResponseEntity<ApiResponse<String>> uploadCompressedFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = cloudinaryStorageService.uploadCompressedFile(file);
            return ResponseEntity.ok(ApiResponse.success("File uploaded and compressed successfully", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to upload compressed file: " + e.getMessage()));
        }
    }
}