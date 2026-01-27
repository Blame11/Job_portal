package com.jobportal.controller;

import com.jobportal.model.Role;
import com.jobportal.model.User;
import jakarta.servlet.http.Cookie;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;
    private String adminUserId;
    private String userId;

    @BeforeEach
    public void setUp() {
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
        adminUserId = admin.getId();
        adminToken = jwtTokenProvider.generateToken(admin.getId());

        // Create regular user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@gmail.com");
        user.setPassword(passwordEncoder.encode("UserPass123!"));
        user.setRole(Role.USER);
        user.setLocation("NYC");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        userId = user.getId();
        userToken = jwtTokenProvider.generateToken(user.getId());
    }

    @Test
    public void testGetAllUsersAsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .cookie(new Cookie("jobPortalToken", adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result", hasSize(2)));
    }

    @Test
    public void testGetAllUsersWithoutAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .cookie(new Cookie("jobPortalToken", userToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + userId)
                .cookie(new Cookie("jobPortalToken", userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result.username").value("testuser"))
                .andExpect(jsonPath("$.result.email").value("testuser@gmail.com"));
    }

    @Test
    public void testGetNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/nonexistent-id")
                .cookie(new Cookie("jobPortalToken", userToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    public void testUpdateUserProfile() throws Exception {
        mockMvc.perform(patch("/api/v1/users")
                .param("username", "updateduser")
                .param("location", "San Francisco")
                .param("gender", "Male")
                .cookie(new Cookie("jobPortalToken", userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result.username").value("updateduser"))
                .andExpect(jsonPath("$.result.location").value("San Francisco"))
                .andExpect(jsonPath("$.result.gender").value("Male"));
    }

    @Test
    public void testDeleteUserAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + userId)
                .cookie(new Cookie("jobPortalToken", adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    public void testDeleteUserWithoutAdminRole() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + userId)
                .cookie(new Cookie("jobPortalToken", userToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateUserRole() throws Exception {
        String updateRequest = "{\"role\": \"RECRUITER\"}";

        mockMvc.perform(patch("/api/v1/users/" + userId + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("jobPortalToken", adminToken))
                .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.result.role").value("RECRUITER"));
    }

    @Test
    public void testUpdateUserRoleWithoutAdminRole() throws Exception {
        String updateRequest = "{\"role\": \"ADMIN\"}";

        mockMvc.perform(patch("/api/v1/users/" + userId + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("jobPortalToken", userToken))
                .content(updateRequest))
                .andExpect(status().isForbidden());
    }
}
