// FILE: org/example/mybatisdemo/controller/ChangeRequestController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.ChangeRequest;
import org.example.roomschedulerapi.service.ChangeRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/change-requests")
@Tag(name = "Change Request Controller", description = "APIs for managing class change requests")
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;

    public ChangeRequestController(ChangeRequestService changeRequestService) {
        this.changeRequestService = changeRequestService;
    }

    @GetMapping
    @Operation(summary = "Get all change requests")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all change requests")
    public List<ChangeRequest> getAllChangeRequests() {
        return changeRequestService.getAllChangeRequests();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get change request by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved change request"),
            @ApiResponse(responseCode = "404", description = "Change request not found")
    })
    public ResponseEntity<ChangeRequest> getChangeRequestById(@Parameter(description = "ID of the change request") @PathVariable Integer id) {
        ChangeRequest request = changeRequestService.getChangeRequestById(id);
        return (request != null) ? ResponseEntity.ok(request) : ResponseEntity.notFound().build();
    }

    @GetMapping("/instructor/{instructorId}")
    @Operation(summary = "Get change requests by Instructor ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved change requests for the instructor")
    public List<ChangeRequest> getChangeRequestsByInstructorId(@Parameter(description = "ID of the instructor") @PathVariable Integer instructorId) {
        return changeRequestService.getChangeRequestsByInstructorId(instructorId);
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get change requests by Class ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved change requests for the class")
    public List<ChangeRequest> getChangeRequestsByClassId(@Parameter(description = "ID of the class") @PathVariable Integer classId) {
        return changeRequestService.getChangeRequestsByClassId(classId);
    }

    @PostMapping
    @Operation(summary = "Create a new change request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created change request"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., instructor, class, or room not found)")
    })
    public ResponseEntity<ChangeRequest> createChangeRequest(@Parameter(description = "Change request object to be created") @RequestBody ChangeRequest changeRequest) {
        // Ensure 'status' and 'requestedAt' are handled (either by client, or set default in service/DB)
        if (changeRequest.getStatus() == null) {
            changeRequest.setStatus("pending"); // Default status if not provided
        }
        ChangeRequest createdRequest = changeRequestService.createChangeRequest(changeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing change request (primarily for status changes by admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated change request"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Change request not found")
    })
    public ResponseEntity<ChangeRequest> updateChangeRequest(
            @Parameter(description = "ID of the change request to update") @PathVariable Integer id,
            @Parameter(description = "Updated change request object") @RequestBody ChangeRequest changeRequestDetails) {
        ChangeRequest updatedRequest = changeRequestService.updateChangeRequest(id, changeRequestDetails);
        return (updatedRequest != null) ? ResponseEntity.ok(updatedRequest) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an existing change request (e.g., status by admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated change request status"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Change request not found")
    })
    public ResponseEntity<ChangeRequest> partialUpdateChangeRequest(
            @Parameter(description = "ID of the change request to update") @PathVariable Integer id,
            @Parameter(description = "Change request object with fields to update (typically status)") @RequestBody ChangeRequest changeRequestDetails) {
        ChangeRequest updatedRequest = changeRequestService.partialUpdateChangeRequest(id, changeRequestDetails);
        return (updatedRequest != null) ? ResponseEntity.ok(updatedRequest) : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a change request (use with caution, typically requests are archived or status changed)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted change request"),
            @ApiResponse(responseCode = "404", description = "Change request not found")
    })
    public ResponseEntity<Void> deleteChangeRequest(@Parameter(description = "ID of the change request to delete") @PathVariable Integer id) {
        changeRequestService.deleteChangeRequest(id);
        return ResponseEntity.noContent().build();
    }
}