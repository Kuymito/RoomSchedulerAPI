package org.example.roomschedulerapi.classroomscheduler.model.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomAvailabilityDto {
    /**
     * The day of the week (e.g., "Mon", "Tue").
     */
    private String dayOfWeek;

    /**
     * The number of available rooms on that day for the selected shift.
     */
    private int availableRooms;
}