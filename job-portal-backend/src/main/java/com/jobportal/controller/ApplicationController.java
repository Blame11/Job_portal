package com.jobportal.controller;

import com.jobportal.dto.ApiResponse;
import com.jobportal.dto.ApplicationWithJobDTO;
import com.jobportal.dto.PaginatedResponse;
import com.jobportal.dto.UpdateApplicationStatusRequest;
import com.jobportal.model.Application;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.FileUploadService;
import com.jobportal.service.JobService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/application")
public class ApplicationController {
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Value("${upload.dir:public/uploads/}")
    private String uploadDir;

    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Application>>> getMyApplications() {
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Application> applications = applicationService.getApplicationsByApplicantId(userId);
            return ResponseEntity.ok(ApiResponse.<List<Application>>builder()
                    .status(true)
                    .result(applications)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<Application>>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/applicant-jobs")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ApplicationWithJobDTO>>> getApplicantJobs() {
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            List<ApplicationWithJobDTO> applications = applicationService.getApplicationsByApplicantIdWithJobs(userId);
            return ResponseEntity.ok(ApiResponse.<List<ApplicationWithJobDTO>>builder()
                    .status(true)
                    .result(applications)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<ApplicationWithJobDTO>>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Application>> applyForJob(
            @RequestParam String jobId,
            @RequestParam(required = false) MultipartFile resume) {
        try {
            String applicantId = SecurityContextHolder.getContext().getAuthentication().getName();
            
            Job job = jobService.getJobById(jobId)
                    .orElseThrow(() -> new Exception("Job not found"));
            
            String resumePath = null;
            if (resume != null && !resume.isEmpty()) {
                resumePath = fileUploadService.uploadFile(resume);
            }
            
            Application application = applicationService.applyForJob(applicantId, job.getCreatedBy(), jobId, resumePath);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Application>builder()
                    .status(true)
                    .result(application)
                    .message("Applied successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Application>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/recruiter-applications")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<PaginatedResponse<Application>> getRecruiterApplications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            String recruiterId = SecurityContextHolder.getContext().getAuthentication().getName();
            Page<Application> applicationPage = applicationService.getApplicationsByRecruiterId(recruiterId, page, limit);
            
            return ResponseEntity.ok(PaginatedResponse.<Application>builder()
                    .status(true)
                    .result(applicationPage.getContent())
                    .totalJobs(applicationPage.getTotalElements())
                    .currentPage(page)
                    .pageCount((int) Math.ceil((double) applicationPage.getTotalElements() / limit))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    PaginatedResponse.<Application>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Application>> updateApplicationStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        try {
            String recruiterId = SecurityContextHolder.getContext().getAuthentication().getName();
            ApplicationStatus status = ApplicationStatus.valueOf(request.getStatus().toUpperCase());
            
            Application application = applicationService.updateApplicationStatus(id, status, recruiterId);
            
            return ResponseEntity.ok(ApiResponse.<Application>builder()
                    .status(true)
                    .result(application)
                    .message("Application status updated successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Application>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}/download-resume")
    @PreAuthorize("isAuthenticated()")
    public void downloadResume(@PathVariable String id, HttpServletResponse response) {
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            
            Application application = applicationService.getApplicationById(id)
                    .orElseThrow(() -> new Exception("Application not found"));
            
            if (!applicationService.isApplicantOrRecruiter(id, userId)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }
            
            if (application.getResume() == null || application.getResume().isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }
            
            String filename = application.getResume().replace("/uploads/", "");
            String filePath = uploadDir + filename;
            
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }
            
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            try {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            } catch (Exception ex) {
                // Ignore
            }
        } catch (Exception e) {
            try {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            } catch (Exception ex) {
                // Ignore
            }
        }
    }
}
