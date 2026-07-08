package com.kshitij.collegeerp.dashboard.service;

import com.kshitij.collegeerp.dashboard.dto.DashboardResponse;
import org.springframework.security.core.Authentication;

public interface DashboardService {

    DashboardResponse getDashboard(Authentication authentication);

}