package org.example.roomschedulerapi.classroomscheduler.model.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDto {
    /**
     * Count of classes assigned to a room in the schedule.
     * Corresponds to 'Class Assign' card.
     */
    private long classAssignCount;

    /**
     * Count of expired change requests.
     * Corresponds to 'Expired' card.
     */
    private long expiredRequestsCount;

    /**
     * Count of classes that are not yet assigned to any room in the schedule.
     * Corresponds to 'Unassigned Class' card.
     */
    private long unassignedClassCount;

    /**
     * Count of classes that are designated as online.
     * Corresponds to 'Online Class' card.
     */
    private long onlineClassCount;

    /**
     * Data for the 'Room Available' chart, showing availability for each day of the week.
     */
    private List<RoomAvailabilityDto> roomAvailability;
}