package com.jobportal.controller;

import com.jobportal.dto.CreateJobRequest;
import com.jobportal.model.Job;
import jakarta.servlet.http.Cookie;

import com.jobportal.model.JobStatus;
import com.jobportal.model.JobType;
import com.jobportal.model.Role;
import com.jobportal.model.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class JobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String recruiterToken;
    private String recruiterUserId;

    @BeforeEach
    public void setUp() {
        jobRepository.deleteAll();
        userRepository.deleteAll();

        // Create recruiter user
        User recruiter = new User();
        recruiter.setUsername("recruiter");
        recruiter.setEmail("recruiter@gmail.com");
        recruiter.setPassword(passwordEncoder.encode("RecruiterPass123!"));
        recruiter.setRole(Role.RECRUITER);
        recruiter.setCreatedAt(LocalDateTime.now());
        recruiter.setUpdatedAt(LocalDateTime.now());
        recruiter = userRepository.save(recruiter);
        
        recruiterUserId = recruiter.getId();
        recruiterToken = jwtTokenProvider.generateToken(recruiter.getId());
    }

    @Test
    public void testCreateJobSuccess() throws Exception {
        CreateJobRequest request = new CreateJobRequest();
        request.setCompany("Tech Company");
        request.setPosition("Senior Developer");
        request.setJobLocation("New York");
        request.setJobVacancy("5");
        request.setJobSalary("$100k");
        request.setJobDeadline("2026-02-26");
        request.setJobDescription("Great job opportunity");
        request.setJobSkills(Arrays.asList("Java", "Spring"));
        request.setJobFacilities(Arrays.asList("Health Insurance", "Free Lunch"));
        request.setJobContact("contact@company.com");
        request.setJobType("full-time");

        mockMvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("jobPortalToken", recruiterToken))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result.company").value("Tech Company"))
                .andExpect(jsonPath("$.result.position").value("Senior Developer"));
    }

    @Test
    public void testGetAllJobs() throws Exception {
        // Create sample job
        Job job = new Job();
        job.setCompany("Sample Company");
        job.setPosition("Java Developer");
        job.setJobLocation("NYC");
        job.setCreatedBy(recruiterUserId);
        job.setJobVacancy("3");
        job.setJobSalary("$80k");
        job.setJobDeadline("2026-02-26");
        job.setJobDescription("Java job");
        job.setJobSkills(Arrays.asList("Java"));
        job.setJobFacilities(Arrays.asList("Insurance"));
        job.setJobContact("contact@company.com");
        job.setJobType(JobType.FULL_TIME);
        job.setJobStatus(JobStatus.PENDING);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);

        mockMvc.perform(get("/api/v1/jobs")
                .param("page", "1")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(jsonPath("$.totalJobs").value(1))
                .andExpect(jsonPath("$.currentPage").value(1));
    }

    @Test
    public void testGetJobById() throws Exception {
        Job job = new Job();
        job.setCompany("Sample Company");
        job.setPosition("Java Developer");
        job.setJobLocation("NYC");
        job.setCreatedBy(recruiterUserId);
        job.setJobVacancy("3");
        job.setJobSalary("$80k");
        job.setJobDeadline("2026-02-26");
        job.setJobDescription("Java job");
        job.setJobSkills(Arrays.asList("Java"));
        job.setJobFacilities(Arrays.asList("Insurance"));
        job.setJobContact("contact@company.com");
        job.setJobType(JobType.FULL_TIME);
        job.setJobStatus(JobStatus.PENDING);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job = jobRepository.save(job);

        mockMvc.perform(get("/api/v1/jobs/" + job.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result.company").value("Sample Company"));
    }

    @Test
    public void testDeleteJobUnauthorized() throws Exception {
        Job job = new Job();
        job.setCompany("Sample Company");
        job.setPosition("Java Developer");
        job.setJobLocation("NYC");
        job.setCreatedBy("other-recruiter-id");
        job.setJobVacancy("3");
        job.setJobSalary("$80k");
        job.setJobDeadline("2026-02-26");
        job.setJobDescription("Java job");
        job.setJobSkills(Arrays.asList("Java"));
        job.setJobFacilities(Arrays.asList("Insurance"));
        job.setJobContact("contact@company.com");
        job.setJobType(JobType.FULL_TIME);
        job.setJobStatus(JobStatus.PENDING);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job = jobRepository.save(job);

        mockMvc.perform(delete("/api/v1/jobs/" + job.getId())
                .cookie(new Cookie("jobPortalToken", recruiterToken)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value(containsString("not authorized")));
    }

    @Test
    public void testGetMyJobs() throws Exception {
        Job job1 = new Job();
        job1.setCompany("Company1");
        job1.setPosition("Dev1");
        job1.setJobLocation("NYC");
        job1.setCreatedBy(recruiterUserId);
        job1.setJobVacancy("1");
        job1.setJobSalary("80k");
        job1.setJobDeadline("2026-02-26");
        job1.setJobDescription("Job");
        job1.setJobSkills(Arrays.asList("Java"));
        job1.setJobFacilities(Arrays.asList("Insurance"));
        job1.setJobContact("contact@company.com");
        job1.setJobType(JobType.FULL_TIME);
        job1.setJobStatus(JobStatus.PENDING);
        job1.setCreatedAt(LocalDateTime.now());
        job1.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job1);

        mockMvc.perform(get("/api/v1/jobs/my-jobs")
                .cookie(new Cookie("jobPortalToken", recruiterToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result", hasSize(1)));
    }
}
