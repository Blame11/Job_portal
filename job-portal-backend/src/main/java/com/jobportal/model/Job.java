package com.jobportal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "Job")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    @Id
    private String id;
    
    private String company;
    private String position;
    private JobStatus jobStatus;
    private JobType jobType;
    private String jobLocation;
    private String createdBy;
    private String jobVacancy;
    private String jobSalary;
    private String jobDeadline;
    private String jobDescription;
    private List<String> jobSkills;
    private List<String> jobFacilities;
    private String jobContact;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
