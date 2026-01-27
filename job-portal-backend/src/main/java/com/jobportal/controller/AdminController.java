package com.jobportal.controller;

import com.jobportal.dto.AdminStatsResponse;
import com.jobportal.dto.ApiResponse;
import com.jobportal.dto.MonthlyStatsResponse;
import com.jobportal.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @GetMapping("/info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getSystemStats() {
        try {
            AdminStatsResponse stats = adminService.getSystemStats();
            return ResponseEntity.ok(ApiResponse.<AdminStatsResponse>builder()
                    .status(true)
                    .result(stats)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<AdminStatsResponse>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/monthly-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MonthlyStatsResponse>>> getMonthlyStats() {
        try {
            List<MonthlyStatsResponse> stats = adminService.getMonthlyStats();
            return ResponseEntity.ok(ApiResponse.<List<MonthlyStatsResponse>>builder()
                    .status(true)
                    .result(stats)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<MonthlyStatsResponse>>builder()
                            .status(false)
                            .message(e.getMessage())
                            .build());
        }
    }
}
