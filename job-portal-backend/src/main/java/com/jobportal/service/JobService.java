package com.jobportal.service;

import com.jobportal.dto.CreateJobRequest;
import com.jobportal.model.Job;
import com.jobportal.model.JobStatus;
import com.jobportal.model.JobType;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;

    public Job createJob(CreateJobRequest request, String recruiterId) throws Exception {
        Job job = new Job();
        job.setCompany(request.getCompany().trim());
        job.setPosition(request.getPosition().trim());
        job.setJobLocation(request.getJobLocation().trim());
        job.setCreatedBy(recruiterId);
        job.setJobVacancy(request.getJobVacancy());
        job.setJobSalary(request.getJobSalary());
        job.setJobDeadline(request.getJobDeadline());
        job.setJobDescription(request.getJobDescription().trim());
        job.setJobSkills(request.getJobSkills());
        job.setJobFacilities(request.getJobFacilities());
        job.setJobContact(request.getJobContact().trim());
        
        job.setJobType(JobType.fromDisplayValue(
            request.getJobType() != null ? request.getJobType() : "full-time"
        ));
        job.setJobStatus(request.getJobStatus() != null ? 
            JobStatus.valueOf(request.getJobStatus().toUpperCase()) : JobStatus.PENDING);
        
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        
        return jobRepository.save(job);
    }

    public Optional<Job> getJobById(String jobId) {
        return jobRepository.findById(jobId);
    }

    public Job updateJob(String jobId, CreateJobRequest request, String recruiterId) throws Exception {
        Job job = getJobById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        if (!job.getCreatedBy().equals(recruiterId)) {
            throw new Exception("You are not authorized to update this job");
        }

        if (request.getCompany() != null) job.setCompany(request.getCompany().trim());
        if (request.getPosition() != null) job.setPosition(request.getPosition().trim());
        if (request.getJobLocation() != null) job.setJobLocation(request.getJobLocation().trim());
        if (request.getJobVacancy() != null) job.setJobVacancy(request.getJobVacancy());
        if (request.getJobSalary() != null) job.setJobSalary(request.getJobSalary());
        if (request.getJobDeadline() != null) job.setJobDeadline(request.getJobDeadline());
        if (request.getJobDescription() != null) job.setJobDescription(request.getJobDescription().trim());
        if (request.getJobSkills() != null) job.setJobSkills(request.getJobSkills());
        if (request.getJobFacilities() != null) job.setJobFacilities(request.getJobFacilities());
        if (request.getJobContact() != null) job.setJobContact(request.getJobContact().trim());
        if (request.getJobType() != null) job.setJobType(JobType.fromDisplayValue(request.getJobType()));
        if (request.getJobStatus() != null) job.setJobStatus(JobStatus.valueOf(request.getJobStatus().toUpperCase()));

        job.setUpdatedAt(LocalDateTime.now());
        return jobRepository.save(job);
    }

    public void deleteJob(String jobId, String recruiterId) throws Exception {
        Job job = getJobById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        if (!job.getCreatedBy().equals(recruiterId)) {
            throw new Exception("You are not authorized to delete this job");
        }

        jobRepository.deleteById(jobId);
    }

    public List<Job> getJobsByRecruiterId(String recruiterId) {
        return jobRepository.findByCreatedBy(recruiterId);
    }

    public List<Job> getAllJobs(int page, int limit, String search, String sort) {
        Sort sortOrder = createSort(sort);
        PageRequest pageRequest = PageRequest.of(page - 1, limit, sortOrder);
        
        if (search != null && !search.isBlank()) {
            return jobRepository.searchJobs(search, pageRequest).getContent();
        }
        
        return jobRepository.findAll(pageRequest).getContent();
    }

    public long getTotalJobCount(String search) {
        if (search != null && !search.isBlank()) {
            return jobRepository.countSearchResults(search);
        }
        return jobRepository.count();
    }

    public void updateJobStatus(String jobId, JobStatus status, String recruiterId) throws Exception {
        Job job = getJobById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        if (!job.getCreatedBy().equals(recruiterId)) {
            throw new Exception("You are not authorized to update this job status");
        }

        job.setJobStatus(status);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);

        // Auto-decline all pending applications when job is declined
        if (status == JobStatus.DECLINED) {
            List<com.jobportal.model.Application> pendingApplications = 
                applicationRepository.findByJobIdAndStatus(jobId, com.jobportal.model.ApplicationStatus.PENDING);
            
            for (com.jobportal.model.Application app : pendingApplications) {
                app.setStatus(com.jobportal.model.ApplicationStatus.REJECTED);
                app.setUpdatedAt(LocalDateTime.now());
                applicationRepository.save(app);
            }
        }
    }

    private Sort createSort(String sortType) {
        return switch (sortType) {
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "a-z" -> Sort.by(Sort.Direction.ASC, "company");
            case "z-a" -> Sort.by(Sort.Direction.DESC, "company");
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); // newest
        };
    }
}
