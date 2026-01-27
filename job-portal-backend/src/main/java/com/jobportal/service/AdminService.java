package com.jobportal.service;

import com.jobportal.dto.AdminStatsResponse;
import com.jobportal.dto.MonthlyStatsResponse;
import com.jobportal.model.JobStatus;
import com.jobportal.model.Role;
import com.jobportal.repository.UserRepository;
import com.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JobRepository jobRepository;

    public AdminStatsResponse getSystemStats() {
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.countByRole(Role.ADMIN);
        long totalRecruiters = userRepository.countByRole(Role.RECRUITER);
        long totalApplicants = userRepository.countByRole(Role.USER);
        
        long totalJobs = jobRepository.count();
        long pendingJobs = jobRepository.countByJobStatus(JobStatus.PENDING);
        long interviewJobs = jobRepository.countByJobStatus(JobStatus.INTERVIEW);
        long declinedJobs = jobRepository.countByJobStatus(JobStatus.DECLINED);
        
        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalAdmins(totalAdmins)
                .totalRecruiters(totalRecruiters)
                .totalApplicants(totalApplicants)
                .totalJobs(totalJobs)
                .pendingJobs(pendingJobs)
                .interviewJobs(interviewJobs)
                .declinedJobs(declinedJobs)
                .build();
    }

    public List<MonthlyStatsResponse> getMonthlyStats() {
        List<MonthlyStatsResponse> monthlyStats = new ArrayList<>();
        
        // Get last 6 months of stats
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        
        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);
            
            // Count jobs created in this month
            long jobCount = jobRepository.findAll().stream()
                    .filter(job -> job.getCreatedAt() != null && 
                            !job.getCreatedAt().isBefore(startOfMonth) && 
                            !job.getCreatedAt().isAfter(endOfMonth))
                    .count();
            
            monthlyStats.add(MonthlyStatsResponse.builder()
                    .month(month.toString())
                    .jobCount(jobCount)
                    .build());
        }
        
        return monthlyStats;
    }
}
