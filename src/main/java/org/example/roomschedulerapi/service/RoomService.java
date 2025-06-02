// FILE: org/example/mybatisdemo/service/RoomService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Room;
import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();
    Room getRoomById(Integer roomId);
    List<Room> getRoomsByBuildingId(Integer buildingId);
    Room createRoom(Room room);
    Room updateRoom(Integer roomId, Room roomDetails);
    Room partialUpdateRoom(Integer roomId, Room roomDetails);
    void deleteRoom(Integer roomId);
}