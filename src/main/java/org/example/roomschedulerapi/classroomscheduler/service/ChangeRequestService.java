package org.example.roomschedulerapi.classroomscheduler.service;

import org.example.roomschedulerapi.classroomscheduler.model.dto.ChangeRequestCreateDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.ChangeRequestResponseDto;
import java.util.List;

public interface ChangeRequestService {
    ChangeRequestResponseDto createChangeRequest(ChangeRequestCreateDto requestDto, Long instructorId);
    List<ChangeRequestResponseDto> getAllChangeRequests();
    ChangeRequestResponseDto approveChangeRequest(Long requestId);
    ChangeRequestResponseDto denyChangeRequest(Long requestId);
}