package com.jobportal.controller;

import com.jobportal.model.*;
import jakarta.servlet.http.Cookie;

import com.jobportal.repository.ApplicationRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class ApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationRepository applicationRepository;

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

    private String userToken;
    private String recruiterToken;
    private String userUserId;
    private String recruiterUserId;
    private String jobId;

    @BeforeEach
    public void setUp() {
        applicationRepository.deleteAll();
        jobRepository.deleteAll();
        userRepository.deleteAll();

        // Create user
        User user = new User();
        user.setUsername("applicant");
        user.setEmail("applicant@gmail.com");
        user.setPassword(passwordEncoder.encode("UserPass123!"));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        userUserId = user.getId();
        userToken = jwtTokenProvider.generateToken(user.getId());

        // Create recruiter
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

        // Create job
        Job job = new Job();
        job.setCompany("Tech Company");
        job.setPosition("Java Developer");
        job.setJobLocation("NYC");
        job.setCreatedBy(recruiterUserId);
        job.setJobVacancy("5");
        job.setJobSalary("$100k");
        job.setJobDeadline("2026-02-26");
        job.setJobDescription("Great opportunity");
        job.setJobSkills(Arrays.asList("Java", "Spring"));
        job.setJobFacilities(Arrays.asList("Insurance", "Lunch"));
        job.setJobContact("contact@company.com");
        job.setJobType(JobType.FULL_TIME);
        job.setJobStatus(JobStatus.PENDING);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job = jobRepository.save(job);
        jobId = job.getId();
    }

    @Test
    public void testApplyForJobSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/application/apply")
                .param("jobId", jobId)
                .cookie(new Cookie("jobPortalToken", userToken)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result.applicantId").value(userUserId))
                .andExpect(jsonPath("$.result.recruiterId").value(recruiterUserId))
                .andExpect(jsonPath("$.result.jobId").value(jobId))
                .andExpect(jsonPath("$.result.status").value("PENDING"));
    }

    @Test
    public void testApplyForJobDuplicate() throws Exception {
        // First application
        Application application = new Application();
        application.setApplicantId(userUserId);
        application.setRecruiterId(recruiterUserId);
        application.setJobId(jobId);
        application.setStatus(ApplicationStatus.PENDING);
        application.setDateOfApplication(LocalDate.now());
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(application);

        // Try to apply again
        mockMvc.perform(post("/api/v1/application/apply")
                .param("jobId", jobId)
                .cookie(new Cookie("jobPortalToken", userToken)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("Already Applied"));
    }

    @Test
    public void testGetMyApplications() throws Exception {
        // Create application
        Application application = new Application();
        application.setApplicantId(userUserId);
        application.setRecruiterId(recruiterUserId);
        application.setJobId(jobId);
        application.setStatus(ApplicationStatus.PENDING);
        application.setDateOfApplication(LocalDate.now());
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(application);

        mockMvc.perform(get("/api/v1/application")
                .cookie(new Cookie("jobPortalToken", userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result", hasSize(1)));
    }

    @Test
    public void testGetRecruiterApplications() throws Exception {
        // Create application
        Application application = new Application();
        application.setApplicantId(userUserId);
        application.setRecruiterId(recruiterUserId);
        application.setJobId(jobId);
        application.setStatus(ApplicationStatus.PENDING);
        application.setDateOfApplication(LocalDate.now());
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(application);

        mockMvc.perform(get("/api/v1/application/recruiter-applications")
                .cookie(new Cookie("jobPortalToken", recruiterToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(jsonPath("$.totalJobs").value(1));
    }

    @Test
    public void testUpdateApplicationStatus() throws Exception {
        // Create application
        Application application = new Application();
        application.setApplicantId(userUserId);
        application.setRecruiterId(recruiterUserId);
        application.setJobId(jobId);
        application.setStatus(ApplicationStatus.PENDING);
        application.setDateOfApplication(LocalDate.now());
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        application = applicationRepository.save(application);

        String updateRequest = "{\"status\": \"ACCEPTED\"}";

        mockMvc.perform(patch("/api/v1/application/" + application.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("jobPortalToken", recruiterToken))
                .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result.status").value("ACCEPTED"));
    }

    @Test
    public void testUpdateApplicationStatusUnauthorized() throws Exception {
        // Create application
        Application application = new Application();
        application.setApplicantId(userUserId);
        application.setRecruiterId("other-recruiter-id");
        application.setJobId(jobId);
        application.setStatus(ApplicationStatus.PENDING);
        application.setDateOfApplication(LocalDate.now());
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        application = applicationRepository.save(application);

        String updateRequest = "{\"status\": \"ACCEPTED\"}";

        mockMvc.perform(patch("/api/v1/application/" + application.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("jobPortalToken", recruiterToken))
                .content(updateRequest))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    public void testApplyForJobWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/application/apply")
                .param("jobId", jobId))
                .andExpect(status().isUnauthorized());
    }
}
