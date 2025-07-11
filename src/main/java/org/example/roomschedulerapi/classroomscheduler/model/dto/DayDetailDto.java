package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Ensure this constructor includes the new field
public class DayDetailDto {
    private String dayOfWeek;
    private boolean isOnline;
    private String instructorName; // Added field
}