package com.codehaven.backend.controller;

import com.codehaven.backend.application.dto.auth.ApiResponse;
import com.codehaven.backend.security.CurrentUser;
import com.codehaven.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.upload.max-size:10485760}") // 10MB default
    private long maxFileSize;
    
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @CurrentUser UserPrincipal userPrincipal) {
        
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Please select a file to upload"));
            }
            
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "File size exceeds maximum limit of " + (maxFileSize / 1024 / 1024) + "MB"));
            }
            
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Only image files are allowed"));
            }
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, "avatars");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            String newFileName = userPrincipal.getId() + "_" + UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(newFileName);
            
            // Save file
            Files.copy(file.getInputStream(), filePath);
            
            // Return file URL
            String fileUrl = "/api/upload/files/avatars/" + newFileName;
            
            log.info("Avatar uploaded successfully for user {} at {}", userPrincipal.getId(), fileUrl);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "File uploaded successfully",
                    "url", fileUrl,
                    "fileName", newFileName
            ));
            
        } catch (IOException e) {
            log.error("Error uploading avatar for user {}", userPrincipal.getId(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "Failed to upload file: " + e.getMessage()));
        }
    }
    
    @PostMapping("/project-image")
    public ResponseEntity<?> uploadProjectImage(
            @RequestParam("file") MultipartFile file,
            @CurrentUser UserPrincipal userPrincipal) {
        
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Please select a file to upload"));
            }
            
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "File size exceeds maximum limit of " + (maxFileSize / 1024 / 1024) + "MB"));
            }
            
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Only image files are allowed"));
            }
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, "projects");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            String newFileName = "project_" + userPrincipal.getId() + "_" + UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(newFileName);
            
            // Save file
            Files.copy(file.getInputStream(), filePath);
            
            // Return file URL
            String fileUrl = "/api/upload/files/projects/" + newFileName;
            
            log.info("Project image uploaded successfully for user {} at {}", userPrincipal.getId(), fileUrl);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "File uploaded successfully",
                    "url", fileUrl,
                    "fileName", newFileName
            ));
            
        } catch (IOException e) {
            log.error("Error uploading project image for user {}", userPrincipal.getId(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "Failed to upload file: " + e.getMessage()));
        }
    }
    
    @GetMapping("/files/avatars/{fileName}")
    public ResponseEntity<Resource> getAvatarFile(@PathVariable String fileName) {
        return getFile("avatars", fileName);
    }
    
    @GetMapping("/files/projects/{fileName}")
    public ResponseEntity<Resource> getProjectFile(@PathVariable String fileName) {
        return getFile("projects", fileName);
    }
    
    private ResponseEntity<Resource> getFile(String folder, String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, folder, fileName);
            
            if (!Files.exists(filePath)) {
                log.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // Security check - ensure file is within upload directory
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            if (!filePath.toAbsolutePath().startsWith(uploadPath)) {
                log.warn("Security violation: file path {} is outside upload directory {}", filePath, uploadPath);
                return ResponseEntity.badRequest().build();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                log.warn("File is not readable: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // Determine content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
                    
        } catch (MalformedURLException e) {
            log.error("Malformed URL for file: {}/{}", folder, fileName, e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Error serving file: {}/{}", folder, fileName, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
