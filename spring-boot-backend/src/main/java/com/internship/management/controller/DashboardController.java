package com.internship.management.controller;

import com.internship.management.dto.DashboardDTO;
import com.internship.management.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metrics")
    public ResponseEntity<DashboardDTO.DashboardMetrics> getMetrics(
            @RequestParam Long userId
    ) {
        try {
            DashboardDTO.DashboardMetrics metrics = dashboardService.getMetrics(userId);
            return ResponseEntity.ok(metrics);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DashboardDTO.DepartmentStats>> getDepartmentStats() {
        return ResponseEntity.ok(dashboardService.getDepartmentStats());
    }

    @GetMapping("/project-status")
    public ResponseEntity<DashboardDTO.ProjectStatusStats> getProjectStatusStats() {
        return ResponseEntity.ok(dashboardService.getProjectStatusStats());
    }

    @GetMapping("/task-stats")
    public ResponseEntity<DashboardDTO.TaskStats> getTaskStats() {
        return ResponseEntity.ok(dashboardService.getTaskStats());
    }
}
