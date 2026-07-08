package com.kshitij.collegeerp.dashboard.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.dashboard.dto.DashboardResponse;
import com.kshitij.collegeerp.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            Authentication authentication
    ) {

        return ResponseEntity.ok(

                ApiResponse.success(

                        "Dashboard fetched successfully",

                        dashboardService.getDashboard(authentication)

                )

        );

    }

}