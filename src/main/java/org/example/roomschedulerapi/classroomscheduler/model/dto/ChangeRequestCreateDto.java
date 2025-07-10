package org.example.roomschedulerapi.classroomscheduler.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ChangeRequestCreateDto {

    private Long instructorId; // Optional: for admins creating on behalf of an instructor

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    private String description;

    @NotNull(message = "Date of change is required")
    private LocalDateTime dayOfChange;
}