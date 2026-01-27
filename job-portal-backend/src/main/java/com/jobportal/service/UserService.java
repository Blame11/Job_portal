package com.jobportal.service;

import com.jobportal.dto.RegisterRequest;
import com.jobportal.model.Role;
import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

    public User registerUser(RegisterRequest request) throws Exception {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (request.getPassword() == null || request.getConfirmPassword() == null) {
            throw new IllegalArgumentException("Password and Confirm Password are required");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // First user is admin
        long userCount = userRepository.count();
        if (userCount == 0) {
            user.setRole(Role.ADMIN);
        } else if ("admin".equalsIgnoreCase(request.getRole())) {
            if (request.getAdminCode() == null || !"IAMADMIN".equals(request.getAdminCode())) {
                throw new IllegalArgumentException("Invalid admin code");
            }
            user.setRole(Role.ADMIN);
        } else if ("recruiter".equalsIgnoreCase(request.getRole())) {
            user.setRole(Role.RECRUITER);
        } else {
            user.setRole(Role.USER);
        }

        return userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserById(String userId) throws Exception {
        return userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String userId) throws Exception {
        User user = getUserById(userId);
        userRepository.deleteById(userId);
    }

    public User updateUserProfile(String userId, User updates) throws Exception {
        User user = getUserById(userId);
        
        if (updates.getUsername() != null && !updates.getUsername().isBlank()) {
            user.setUsername(updates.getUsername());
        }
        if (updates.getLocation() != null && !updates.getLocation().isBlank()) {
            user.setLocation(updates.getLocation());
        }
        if (updates.getGender() != null && !updates.getGender().isBlank()) {
            user.setGender(updates.getGender());
        }
        if (updates.getResume() != null && !updates.getResume().isBlank()) {
            user.setResume(updates.getResume());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void updateUserRole(String userId, Role role) throws Exception {
        User user = getUserById(userId);
        user.setRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
