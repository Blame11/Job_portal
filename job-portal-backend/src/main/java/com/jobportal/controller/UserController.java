package com.jobportal.controller;

import com.jobportal.dto.ApiResponse;
import com.jobportal.dto.UpdateRoleRequest;
import com.jobportal.model.Role;
import com.jobportal.model.User;
import com.jobportal.service.FileUploadService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.<List<User>>builder()
                    .status(true)
                    .result(users)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<User>>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PatchMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) MultipartFile resume) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            User updates = new User();
            updates.setUsername(username);
            updates.setLocation(location);
            updates.setGender(gender);
            
            if (resume != null && !resume.isEmpty()) {
                String resumePath = fileUploadService.uploadFile(resume);
                updates.setResume(resumePath);
            }
            
            User updatedUser = userService.updateUserProfile(currentUser.getId(), updates);
            return ResponseEntity.ok(ApiResponse.<User>builder()
                    .status(true)
                    .result(updatedUser)
                    .message("Profile updated successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<User>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable String id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.<User>builder()
                    .status(true)
                    .result(user)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<User>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .status(true)
                    .message("User deleted successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<String>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUserRole(
            @PathVariable String id,
            @RequestBody UpdateRoleRequest request) {
        try {
            Role role = Role.valueOf(request.getRole().toUpperCase());
            userService.updateUserRole(id, role);
            
            User user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.<User>builder()
                    .status(true)
                    .result(user)
                    .message("User role updated successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<User>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }
}
