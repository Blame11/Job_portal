package com.jobportal.applicationservice.service;

import com.jobportal.applicationservice.dto.ApplicationCountDTO;
import com.jobportal.applicationservice.dto.ApplicationResponse;
import com.jobportal.applicationservice.model.Application;
import com.jobportal.applicationservice.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final FileUploadService fileUploadService;
    private final RestTemplate restTemplate;

    @Value("${service.urls.job:http://localhost:3003}")
    private String jobServiceUrl;

    public ApplicationResponse applyForJob(String userId, String jobId, MultipartFile resume) throws Exception {
        // Validate job exists
        try {
            String jobUrl = jobServiceUrl + "/internal/jobs/" + jobId;
            restTemplate.getForObject(jobUrl, Object.class);
            log.info("Job validated: {}", jobId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Job not found");
        }

        // Check if already applied
        if (applicationRepository.findByJobIdAndApplicantId(jobId, userId).isPresent()) {
            throw new IllegalArgumentException("You have already applied for this job");
        }

        // Upload resume
        String resumePath = fileUploadService.uploadResume(resume);

        // Get recruiter info (in real scenario, would fetch from job details)
        // For now, using a placeholder
        String recruiterId = jobId; // Will be replaced with actual recruiter ID from job

        Application application = new Application(jobId, userId, recruiterId, resumePath);
        Application savedApp = applicationRepository.save(application);
        
        log.info("Application created: {} for job: {} by user: {}", savedApp.getId(), jobId, userId);
        return mapToResponse(savedApp);
    }

    public List<ApplicationResponse> getApplicantApplications(String userId) {
        return applicationRepository.findByApplicantId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<ApplicationResponse> getRecruiterApplications(String recruiterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return applicationRepository.findByRecruiterId(recruiterId, pageable)
                .map(this::mapToResponse);
    }

    public ApplicationResponse updateApplicationStatus(String applicationId, String userId, String status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!application.getRecruiterId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own applications");
        }

        application.setStatus(status.toUpperCase());
        application.setUpdatedAt(LocalDateTime.now());
        Application updatedApp = applicationRepository.save(application);
        
        log.info("Application status updated: {} to {}", applicationId, status);
        return mapToResponse(updatedApp);
    }

    public ApplicationCountDTO getApplicationCounts() {
        long pending = applicationRepository.countByStatus("PENDING");
        long accepted = applicationRepository.countByStatus("ACCEPTED");
        long rejected = applicationRepository.countByStatus("REJECTED");

        return new ApplicationCountDTO(pending, accepted, rejected);
    }

    private ApplicationResponse mapToResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setJobId(application.getJobId());
        response.setApplicantId(application.getApplicantId());
        response.setRecruiterId(application.getRecruiterId());
        response.setStatus(application.getStatus());
        response.setResumePath(application.getResumePath());
        response.setCreatedAt(application.getCreatedAt() != null ? application.getCreatedAt().toString() : null);
        response.setUpdatedAt(application.getUpdatedAt() != null ? application.getUpdatedAt().toString() : null);
        return response;
    }
}
