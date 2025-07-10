package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClassDayDetailsDto {
    private boolean isOnline;
    private InstructorResponseDto instructor;
}