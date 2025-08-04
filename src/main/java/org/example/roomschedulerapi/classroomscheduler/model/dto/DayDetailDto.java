package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayDetailDto {
    // Original Day Details
    private String dayOfWeek;
    private boolean isOnline;
    private String instructorName;

    // --- Temporary Assignment Details ---
    private String eventName;
    private String temporaryDay; // The new field for the temporary day of the week
    private Long temporaryRoomId;
    private String temporaryRoomName;
    private String temporaryBuildingName;
    private LocalDate effectiveDate;
    private ShiftResponseDto temporaryShift;
}