package org.example.roomschedulerapi.classroomscheduler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.roomschedulerapi.classroomscheduler.model.dto.DashboardResponseDto;
import org.example.roomschedulerapi.classroomscheduler.service.DashboardService;
import org.example.roomschedulerapi.classroomscheduler.service.impl.DashboardServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    /**
     * Retrieves aggregated data for the main dashboard view.
     * The 'Room Available' chart data can be filtered by a specific shift.
     *
     * @param shiftId Optional ID of the shift to calculate room availability for.
     *                If not provided, a default morning shift is used.
     * @return A ResponseEntity containing the DashboardResponseDto.
     */
    @GetMapping
    public ResponseEntity<DashboardResponseDto> getDashboardStatistics(
            @RequestParam(required = false) Long shiftId) {

        DashboardResponseDto dashboardData = dashboardService.getDashboardData(shiftId);
        return ResponseEntity.ok(dashboardData);
    }

}