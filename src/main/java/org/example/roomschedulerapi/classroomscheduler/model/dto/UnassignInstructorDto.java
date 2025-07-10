package org.example.roomschedulerapi.classroomscheduler.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnassignInstructorDto {

    @NotNull(message = "Class ID is required.")
    private Long classId;

    @NotBlank(message = "Day of the week is required.")
    private String dayOfWeek;
}