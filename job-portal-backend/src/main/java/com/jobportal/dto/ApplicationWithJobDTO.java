package com.jobportal.dto;

import com.jobportal.model.ApplicationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ApplicationWithJobDTO {
    private String id;
    private String applicantId;
    private String recruiterId;
    private String jobId;
    private String position;
    private String company;
    private String jobLocation;
    private ApplicationStatus status;
    private String resume;
    private LocalDate dateOfApplication;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ApplicationWithJobDTO() {}

    public ApplicationWithJobDTO(String id, String applicantId, String recruiterId, String jobId,
                                  String position, String company, String jobLocation, ApplicationStatus status,
                                  String resume, LocalDate dateOfApplication, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.applicantId = applicantId;
        this.recruiterId = recruiterId;
        this.jobId = jobId;
        this.position = position;
        this.company = company;
        this.jobLocation = jobLocation;
        this.status = status;
        this.resume = resume;
        this.dateOfApplication = dateOfApplication;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(String recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public LocalDate getDateOfApplication() {
        return dateOfApplication;
    }

    public void setDateOfApplication(LocalDate dateOfApplication) {
        this.dateOfApplication = dateOfApplication;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
