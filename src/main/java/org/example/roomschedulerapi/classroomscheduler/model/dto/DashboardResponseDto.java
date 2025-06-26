package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDto {

    /**
     * Count of classes that have been assigned to a room and time in the schedule.
     */
    private long classAssignCount;

    /**
     * Count of items marked as expired (e.g., change requests).
     */
    private long expiredCount;

    /**
     * Count of classes that exist but are not yet assigned to any schedule.
     */
    private long unassignedClassCount;

    /**
     * Count of classes that are designated as online.
     */
    private long onlineClassCount;

    /**
     * A map representing room availability for a typical week for a given shift.
     * Key: Day of the week (e.g., "Mon", "Tue").
     * Value: Number of available rooms.
     */
    private Map<String, Long> roomAvailability;
}