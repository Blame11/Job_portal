package com.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long totalAdmins;
    private long totalRecruiters;
    private long totalApplicants;
    private long totalJobs;
    private long pendingJobs;
    private long interviewJobs;
    private long declinedJobs;
}
