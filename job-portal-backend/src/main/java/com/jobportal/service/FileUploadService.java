package com.jobportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {
    
    @Value("${upload.dir:public/uploads/}")
    private String uploadDir;
    
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "pdf", "doc", "docx"
    );

    public String uploadFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception("File is empty");
        }

        // Validate file type
        String mimeType = file.getContentType();
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new Exception("Invalid file type. Only PDF, DOC, DOCX are allowed");
        }

        // Validate file extension
        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new Exception("Invalid file extension. Only PDF, DOC, DOCX are allowed");
        }

        // Create upload directory if it doesn't exist
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        // Generate unique filename
        String uniqueFilename = UUID.randomUUID() + "." + extension;
        Path filePath = Paths.get(uploadDir, uniqueFilename);

        // Save file
        Files.write(filePath, file.getBytes());

        // Return relative path
        return "/uploads/" + uniqueFilename;
    }

    public void downloadFile(String filename) throws Exception {
        Path filePath = Paths.get(uploadDir, filename);
        
        if (!Files.exists(filePath)) {
            throw new Exception("File not found");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
