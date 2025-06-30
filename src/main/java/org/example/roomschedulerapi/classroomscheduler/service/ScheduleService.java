package org.example.roomschedulerapi.classroomscheduler.service;

import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleRequestDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ScheduleResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ScheduleService {

    @Transactional
    ScheduleResponseDto assignRoomToClass(ScheduleRequestDto dto);
    List<ScheduleResponseDto> getAllSchedules();
    Optional<ScheduleResponseDto> getScheduleById(Long scheduleId);
    List<ScheduleResponseDto> getSchedulesForInstructor(Long instructorId);
}
