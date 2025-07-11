package org.example.roomschedulerapi.classroomscheduler.service;

import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ChangeRequestCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ChangeRequestResponseDto;
import java.util.List;

public interface ChangeRequestService {
    public ChangeRequestResponseDto createChangeRequest(ChangeRequestCreateDto requestDto, Instructor instructor);
    List<ChangeRequestResponseDto> getAllChangeRequests();
    ChangeRequestResponseDto approveChangeRequest(Long requestId);
    ChangeRequestResponseDto denyChangeRequest(Long requestId);
}