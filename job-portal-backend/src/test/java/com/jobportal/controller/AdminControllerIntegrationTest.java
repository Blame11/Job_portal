package com.jobportal.controller;
import jakarta.servlet.http.Cookie;

import com.jobportal.model.*;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    public void setUp() {
        jobRepository.deleteAll();
        userRepository.deleteAll();

        // Create admin
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("AdminPass123!"));
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        admin = userRepository.save(admin);
        adminToken = jwtTokenProvider.generateToken(admin.getId());

        // Create recruiter
        User recruiter = new User();
        recruiter.setUsername("recruiter");
        recruiter.setEmail("recruiter@gmail.com");
        recruiter.setPassword(passwordEncoder.encode("RecruiterPass123!"));
        recruiter.setRole(Role.RECRUITER);
        recruiter.setCreatedAt(LocalDateTime.now());
        recruiter.setUpdatedAt(LocalDateTime.now());
        recruiter = userRepository.save(recruiter);

        // Create applicant
        User applicant = new User();
        applicant.setUsername("applicant");
        applicant.setEmail("applicant@gmail.com");
        applicant.setPassword(passwordEncoder.encode("ApplicantPass123!"));
        applicant.setRole(Role.USER);
        applicant.setCreatedAt(LocalDateTime.now());
        applicant.setUpdatedAt(LocalDateTime.now());
        userRepository.save(applicant);

        // Create jobs with different statuses
        Job job1 = new Job();
        job1.setCompany("Company1");
        job1.setPosition("Dev1");
        job1.setJobLocation("NYC");
        job1.setCreatedBy(recruiter.getId());
        job1.setJobVacancy("1");
        job1.setJobSalary("80k");
        job1.setJobDeadline("2026-02-26");
        job1.setJobDescription("Job1");
        job1.setJobSkills(Arrays.asList("Java"));
        job1.setJobFacilities(Arrays.asList("Insurance"));
        job1.setJobContact("contact@company.com");
        job1.setJobType(JobType.FULL_TIME);
        job1.setJobStatus(JobStatus.PENDING);
        job1.setCreatedAt(LocalDateTime.now());
        job1.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job1);

        Job job2 = new Job();
        job2.setCompany("Company2");
        job2.setPosition("Dev2");
        job2.setJobLocation("LA");
        job2.setCreatedBy(recruiter.getId());
        job2.setJobVacancy("2");
        job2.setJobSalary("90k");
        job2.setJobDeadline("2026-03-26");
        job2.setJobDescription("Job2");
        job2.setJobSkills(Arrays.asList("Python"));
        job2.setJobFacilities(Arrays.asList("Insurance"));
        job2.setJobContact("contact2@company.com");
        job2.setJobType(JobType.FULL_TIME);
        job2.setJobStatus(JobStatus.INTERVIEW);
        job2.setCreatedAt(LocalDateTime.now());
        job2.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job2);

        Job job3 = new Job();
        job3.setCompany("Company3");
        job3.setPosition("Dev3");
        job3.setJobLocation("SF");
        job3.setCreatedBy(recruiter.getId());
        job3.setJobVacancy("3");
        job3.setJobSalary("100k");
        job3.setJobDeadline("2026-04-26");
        job3.setJobDescription("Job3");
        job3.setJobSkills(Arrays.asList("Go"));
        job3.setJobFacilities(Arrays.asList("Insurance"));
        job3.setJobContact("contact3@company.com");
        job3.setJobType(JobType.FULL_TIME);
        job3.setJobStatus(JobStatus.DECLINED);
        job3.setCreatedAt(LocalDateTime.now());
        job3.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job3);
    }

    @Test
    public void testGetSystemStats() throws Exception {
        mockMvc.perform(get("/api/v1/admin/info")
                .cookie(new Cookie("jobPortalToken", adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result.totalUsers").value(3))
                .andExpect(jsonPath("$.result.totalAdmins").value(1))
                .andExpect(jsonPath("$.result.totalRecruiters").value(1))
                .andExpect(jsonPath("$.result.totalApplicants").value(1))
                .andExpect(jsonPath("$.result.totalJobs").value(3))
                .andExpect(jsonPath("$.result.pendingJobs").value(1))
                .andExpect(jsonPath("$.result.interviewJobs").value(1))
                .andExpect(jsonPath("$.result.declinedJobs").value(1));
    }

    @Test
    public void testGetMonthlyStats() throws Exception {
        mockMvc.perform(get("/api/v1/admin/monthly-stats")
                .cookie(new Cookie("jobPortalToken", adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result", hasSize(6)))
                .andExpect(jsonPath("$.result[*].jobCount", hasItem(3)));
    }

    @Test
    public void testGetSystemStatsWithoutAdminRole() throws Exception {
        // Create non-admin user
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@gmail.com");
        user.setPassword(passwordEncoder.encode("UserPass123!"));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        String userToken = jwtTokenProvider.generateToken(user.getId());

        mockMvc.perform(get("/api/v1/admin/info")
                .cookie(new Cookie("jobPortalToken", userToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetMonthlyStatsWithoutAdminRole() throws Exception {
        // Create non-admin user
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@gmail.com");
        user.setPassword(passwordEncoder.encode("UserPass123!"));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        String userToken = jwtTokenProvider.generateToken(user.getId());

        mockMvc.perform(get("/api/v1/admin/monthly-stats")
                .cookie(new Cookie("jobPortalToken", userToken)))
                .andExpect(status().isForbidden());
    }
}
