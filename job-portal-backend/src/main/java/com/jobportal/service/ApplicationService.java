package com.jobportal.service;

import com.jobportal.model.Application;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    
    @Autowired
    private ApplicationRepository applicationRepository;

    public Application applyForJob(String applicantId, String recruiterId, String jobId, String resume) throws Exception {
        // Check for duplicate application
        Optional<Application> existingApp = applicationRepository.findByApplicantIdAndJobId(applicantId, jobId);
        if (existingApp.isPresent()) {
            throw new Exception("Already Applied");
        }

        Application application = new Application();
        application.setApplicantId(applicantId);
        application.setRecruiterId(recruiterId);
        application.setJobId(jobId);
        application.setStatus(ApplicationStatus.PENDING);
        application.setResume(resume);
        application.setDateOfApplication(LocalDate.now());
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    public Optional<Application> getApplicationById(String appId) {
        return applicationRepository.findById(appId);
    }

    public List<Application> getApplicationsByApplicantId(String applicantId) {
        return applicationRepository.findByApplicantId(applicantId);
    }

    public Page<Application> getApplicationsByRecruiterId(String recruiterId, int page, int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return applicationRepository.findByRecruiterId(recruiterId, pageRequest);
    }

    public List<Application> getApplicationsByJobId(String jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public Application updateApplicationStatus(String appId, ApplicationStatus status, String recruiterId) throws Exception {
        Application application = getApplicationById(appId)
                .orElseThrow(() -> new Exception("Application not found"));
        
        if (!application.getRecruiterId().equals(recruiterId)) {
            throw new Exception("You are not authorized to update this application");
        }

        application.setStatus(status);
        application.setUpdatedAt(LocalDateTime.now());
        return applicationRepository.save(application);
    }

    public boolean isApplicantOrRecruiter(String applicationId, String userId) throws Exception {
        Application application = getApplicationById(applicationId)
                .orElseThrow(() -> new Exception("Application not found"));
        
        return application.getApplicantId().equals(userId) || application.getRecruiterId().equals(userId);
    }
}
