package org.example.roomschedulerapi.classroomscheduler.controller;

import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.Room;
import org.example.roomschedulerapi.classroomscheduler.model.dto.RoomUpdateDto;
import org.example.roomschedulerapi.classroomscheduler.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/room")
// If not configured globally, add @CrossOrigin
// @CrossOrigin(origins = "http://localhost:3000")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Room>>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        ApiResponse<List<Room>> response = new ApiResponse<>(
                "Rooms retrieved successfully", rooms, HttpStatus.OK, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Room>> getRoomById(@PathVariable Long roomId) {
        return roomService.getRoomById(roomId)
                .map(room -> ResponseEntity.ok(new ApiResponse<>("Room retrieved successfully", room, HttpStatus.OK, LocalDateTime.now())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Room not found with id: " + roomId, null, HttpStatus.NOT_FOUND, LocalDateTime.now())));
    }


    // You now have PATCH for partial updates, PUT for full updates would be similar but use a non-optional DTO
    @PatchMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Room>> patchRoom(@PathVariable Long roomId, @RequestBody RoomUpdateDto roomUpdateDto) {
        try {
            Room patchedRoom = roomService.patchRoom(roomId, roomUpdateDto);
            return ResponseEntity.ok(new ApiResponse<>("Room updated successfully", patchedRoom, HttpStatus.OK, LocalDateTime.now()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("Error updating room: " + e.getMessage(), null, HttpStatus.BAD_REQUEST, LocalDateTime.now()));
        }
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long roomId) {
        try {
            roomService.deleteRoom(roomId);
            return ResponseEntity.ok(new ApiResponse<Void>("Room deleted successfully", null, HttpStatus.OK, LocalDateTime.now()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        }
    }
}