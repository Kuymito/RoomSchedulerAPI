// FILE: org/example/mybatisdemo/controller/RoomController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.Room;
import org.example.roomschedulerapi.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Room Controller", description = "APIs for managing rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(summary = "Get all rooms")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all rooms")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved room"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<Room> getRoomById(@Parameter(description = "ID of the room to retrieve") @PathVariable Integer id) {
        Room room = roomService.getRoomById(id);
        return (room != null) ? ResponseEntity.ok(room) : ResponseEntity.notFound().build();
    }

    @GetMapping("/building/{buildingId}")
    @Operation(summary = "Get rooms by Building ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved rooms for the building")
    public List<Room> getRoomsByBuildingId(@Parameter(description = "ID of the building") @PathVariable Integer buildingId) {
        return roomService.getRoomsByBuildingId(buildingId);
    }

    @PostMapping
    @Operation(summary = "Create a new room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created room"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., building not found, duplicate number in building)")
    })
    public ResponseEntity<Room> createRoom(@Parameter(description = "Room object to be created") @RequestBody Room room) {
        Room createdRoom = roomService.createRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated room"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<Room> updateRoom(
            @Parameter(description = "ID of the room to update") @PathVariable Integer id,
            @Parameter(description = "Updated room object") @RequestBody Room roomDetails) {
        Room updatedRoom = roomService.updateRoom(id, roomDetails);
        return (updatedRoom != null) ? ResponseEntity.ok(updatedRoom) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an existing room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated room"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<Room> partialUpdateRoom(
            @Parameter(description = "ID of the room to update") @PathVariable Integer id,
            @Parameter(description = "Room object with fields to update") @RequestBody Room roomDetails) {
        Room updatedRoom = roomService.partialUpdateRoom(id, roomDetails);
        return (updatedRoom != null) ? ResponseEntity.ok(updatedRoom) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted room"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., room is referenced by classes or requests)")
    })
    public ResponseEntity<Void> deleteRoom(@Parameter(description = "ID of the room to delete") @PathVariable Integer id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}