package com.school.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucket;

    public S3StorageService(
            S3Client s3Client,
            S3Presigner presigner,
            @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.presigner = presigner;
        this.bucket = bucket;
    }

    /**
     * Upload a file and return its S3 key.
     * Folder example: "submissions", "assignments"
     */
    public String upload(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String key = folder + "/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("File uploaded to S3: {}", key);
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a pre-signed GET URL valid for the given duration.
     */
    public String generatePresignedUrl(String key, Duration validity) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(validity)
                .getObjectRequest(r -> r.bucket(bucket).key(key))
                .build();
        return presigner.presignGetObject(presignRequest).url().toString();
    }

    /**
     * Generate a short-lived URL (1 hour) for viewing a file.
     */
    public String getViewUrl(String key) {
        return generatePresignedUrl(key, Duration.ofHours(1));
    }

    /**
     * Delete a file from S3 by key.
     */
    public void delete(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
            log.info("File deleted from S3: {}", key);
        } catch (Exception e) {
            log.warn("Failed to delete file from S3 [{}]: {}", key, e.getMessage());
        }
    }
}
