package org.example.roomschedulerapi.classroomscheduler.service;

import org.example.roomschedulerapi.classroomscheduler.model.dto.DashboardResponseDto;
import org.example.roomschedulerapi.classroomscheduler.service.impl.DashboardServiceImpl;

public interface DashboardService {
//    private final DashboardResponseDto service1;
    /**
     * Gathers and computes all data required for the main dashboard.
     * @param shiftId The shift ID to calculate room availability for. If null, a default shift can be used.
     * @return A DTO containing all dashboard metrics.
     */
    DashboardResponseDto getDashboardData(Long shiftId);
}