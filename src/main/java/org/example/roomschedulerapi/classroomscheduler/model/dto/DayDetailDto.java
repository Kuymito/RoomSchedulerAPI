package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DayDetailDto {
    private String dayOfWeek;
    private boolean isOnline;
}