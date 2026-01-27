package com.jobportal.controller;

import com.jobportal.dto.CreateJobRequest;
import com.jobportal.dto.PaginatedResponse;
import com.jobportal.dto.ApiResponse;
import com.jobportal.dto.UpdateJobStatusRequest;
import com.jobportal.model.Job;
import com.jobportal.model.JobStatus;
import com.jobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.jobportal.model.User;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
    
    @Autowired
    private JobService jobService;

    @GetMapping("")
    public ResponseEntity<PaginatedResponse<Job>> getAllJobs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "newest") String sort) {
        try {
            List<Job> jobs = jobService.getAllJobs(page, limit, search, sort);
            long totalJobs = jobService.getTotalJobCount(search);
            int pageCount = (int) Math.ceil((double) totalJobs / limit);

            return ResponseEntity.ok(PaginatedResponse.<Job>builder()
                    .status(true)
                    .result(jobs)
                    .totalJobs(totalJobs)
                    .currentPage(page)
                    .pageCount(pageCount)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    PaginatedResponse.<Job>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Job>> createJob(@Valid @RequestBody CreateJobRequest request) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Job job = jobService.createJob(request, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Job>builder()
                    .status(true)
                    .result(job)
                    .message("Job created successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Job>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<List<Job>>> getMyJobs() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<Job> jobs = jobService.getJobsByRecruiterId(user.getId());
            return ResponseEntity.ok(ApiResponse.<List<Job>>builder()
                    .status(true)
                    .result(jobs)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<Job>>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Job>> getJobById(@PathVariable String id) {
        try {
            Job job = jobService.getJobById(id)
                    .orElseThrow(() -> new Exception("Job not found"));
            return ResponseEntity.ok(ApiResponse.<Job>builder()
                    .status(true)
                    .result(job)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<Job>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Job>> updateJob(
            @PathVariable String id,
            @Valid @RequestBody CreateJobRequest request) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Job job = jobService.updateJob(id, request, user.getId());
            return ResponseEntity.ok(ApiResponse.<Job>builder()
                    .status(true)
                    .result(job)
                    .message("Job updated successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Job>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<String>> deleteJob(@PathVariable String id) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            jobService.deleteJob(id, user.getId());
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .status(true)
                    .message("Job deleted successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<String>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Job>> updateJobStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateJobStatusRequest request) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            JobStatus status = JobStatus.valueOf(request.getJobStatus().toUpperCase());
            jobService.updateJobStatus(id, status, user.getId());
            
            Job job = jobService.getJobById(id).orElseThrow(() -> new Exception("Job not found"));
            return ResponseEntity.ok(ApiResponse.<Job>builder()
                    .status(true)
                    .result(job)
                    .message("Job status updated successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Job>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }
}
