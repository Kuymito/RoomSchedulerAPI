package org.example.roomschedulerapi.classroomscheduler.service;

import org.example.roomschedulerapi.classroomscheduler.model.Room;
import org.example.roomschedulerapi.classroomscheduler.model.dto.RoomScheduleDto;
import org.example.roomschedulerapi.classroomscheduler.model.dto.RoomUpdateDto;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<RoomScheduleDto> getAllRooms();
    Optional<Room> getRoomById(Long roomId);
    Room patchRoom(Long roomId, RoomUpdateDto roomUpdateDto);
    void deleteRoom(Long roomId);
    Optional<RoomScheduleDto> getRoomSchedule(Long roomId);

}