package com.jobportal.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateJobRequest {
    @NotBlank(message = "Company is required")
    @Size(min = 5, max = 100, message = "Company must be between 5 and 100 characters")
    private String company;
    
    @NotBlank(message = "Position is required")
    @Size(min = 3, max = 100, message = "Position must be between 3 and 100 characters")
    private String position;
    
    @NotBlank(message = "Job location is required")
    private String jobLocation;
    
    @NotBlank(message = "Job vacancy is required")
    private String jobVacancy;
    
    @NotBlank(message = "Job salary is required")
    private String jobSalary;
    
    @NotBlank(message = "Job deadline is required")
    private String jobDeadline;
    
    @NotBlank(message = "Job description is required")
    private String jobDescription;
    
    @NotEmpty(message = "Job skills are required")
    private List<String> jobSkills;
    
    @NotEmpty(message = "Job facilities are required")
    private List<String> jobFacilities;
    
    @NotBlank(message = "Job contact is required")
    private String jobContact;
    
    @Pattern(regexp = "full-time|part-time|internship", message = "Job type must be 'full-time', 'part-time', or 'internship'")
    private String jobType;
    
    @Pattern(regexp = "(?i)pending|interview|declined", message = "Job status must be 'PENDING', 'INTERVIEW', or 'DECLINED'")
    private String jobStatus;
}
