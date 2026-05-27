package com.school.storage;

import com.school.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "Upload files to S3 and get pre-signed download URLs")
public class FileUploadController {

    private final S3StorageService storageService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a file",
               description = "Upload any file (PDF, image, etc.) to S3. " +
                             "Returns the S3 key and a 1-hour pre-signed view URL. " +
                             "Use `folder` param e.g. `submissions` or `assignments`.")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "uploads") String folder) {
        String key = storageService.upload(file, folder);
        String url = storageService.getViewUrl(key);
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully",
                Map.of("key", key, "url", url)));
    }

    @GetMapping("/url")
    @Operation(summary = "Get a fresh pre-signed URL for an existing S3 key")
    public ResponseEntity<ApiResponse<Map<String, String>>> getUrl(@RequestParam String key) {
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", storageService.getViewUrl(key))));
    }

    @DeleteMapping
    @Operation(summary = "Delete a file from S3 by key")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam String key) {
        storageService.delete(key);
        return ResponseEntity.ok(ApiResponse.success("File deleted"));
    }
}
