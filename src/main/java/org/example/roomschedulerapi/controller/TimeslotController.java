// FILE: org/example/mybatisdemo/controller/TimeSlotController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.Timeslot;
import org.example.roomschedulerapi.service.TimeslotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timeslots")
@Tag(name = "TimeSlot Controller", description = "APIs for managing time slots")
public class TimeslotController {

    private final TimeslotService timeSlotService;

    public TimeslotController(TimeslotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    @GetMapping
    @Operation(summary = "Get all time slots")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all time slots")
    public List<Timeslot> getAllTimeSlots() {
        return timeSlotService.getAllTimeSlots();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get time slot by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved time slot"),
            @ApiResponse(responseCode = "404", description = "Time slot not found")
    })
    public ResponseEntity<Timeslot> getTimeSlotById(@Parameter(description = "ID of the time slot") @PathVariable Integer id) {
        Timeslot timeSlot = timeSlotService.getTimeSlotById(id);
        return (timeSlot != null) ? ResponseEntity.ok(timeSlot) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new time slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created time slot"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., overlapping times, invalid day)")
    })
    public ResponseEntity<Timeslot> createTimeSlot(@Parameter(description = "Time slot object to be created") @RequestBody Timeslot timeSlot) {
        // Add validation for time consistency (start_time < end_time) and potential overlaps in service
        Timeslot createdTimeSlot = timeSlotService.createTimeSlot(timeSlot);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTimeSlot);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing time slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated time slot"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Time slot not found")
    })
    public ResponseEntity<Timeslot> updateTimeSlot(
            @Parameter(description = "ID of the time slot to update") @PathVariable Integer id,
            @Parameter(description = "Updated time slot object") @RequestBody Timeslot timeSlotDetails) {
        Timeslot updatedTimeSlot = timeSlotService.updateTimeSlot(id, timeSlotDetails);
        return (updatedTimeSlot != null) ? ResponseEntity.ok(updatedTimeSlot) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an existing time slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated time slot"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Time slot not found")
    })
    public ResponseEntity<Timeslot> partialUpdateTimeSlot(
            @Parameter(description = "ID of the time slot to update") @PathVariable Integer id,
            @Parameter(description = "Time slot object with fields to update") @RequestBody Timeslot timeSlotDetails) {
        Timeslot updatedTimeSlot = timeSlotService.partialUpdateTimeSlot(id, timeSlotDetails);
        return (updatedTimeSlot != null) ? ResponseEntity.ok(updatedTimeSlot) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a time slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted time slot"),
            @ApiResponse(responseCode = "404", description = "Time slot not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., time slot is used by classes)")
    })
    public ResponseEntity<Void> deleteTimeSlot(@Parameter(description = "ID of the time slot to delete") @PathVariable Integer id) {
        // Service layer should check for referential integrity before deleting
        timeSlotService.deleteTimeSlot(id);
        return ResponseEntity.noContent().build();
    }
}