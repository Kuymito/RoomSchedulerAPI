// FILE: org/example/mybatisdemo/controller/BuildingController.java
package org.example.roomschedulerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.roomschedulerapi.model.Building;
import org.example.roomschedulerapi.service.BuildingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buildings")
@Tag(name = "Building Controller", description = "APIs for managing buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    @Operation(summary = "Get all buildings")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all buildings")
    public List<Building> getAllBuildings() {
        return buildingService.getAllBuildings();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get building by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved building"),
            @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public ResponseEntity<Building> getBuildingById(@Parameter(description = "ID of the building to retrieve") @PathVariable Integer id) {
        Building building = buildingService.getBuildingById(id);
        return (building != null) ? ResponseEntity.ok(building) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new building")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created building"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., duplicate name/code)")
    })
    public ResponseEntity<Building> createBuilding(@Parameter(description = "Building object to be created") @RequestBody Building building) {
        Building createdBuilding = buildingService.createBuilding(building);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBuilding);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing building")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated building"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public ResponseEntity<Building> updateBuilding(
            @Parameter(description = "ID of the building to update") @PathVariable Integer id,
            @Parameter(description = "Updated building object") @RequestBody Building buildingDetails) {
        Building updatedBuilding = buildingService.updateBuilding(id, buildingDetails);
        return (updatedBuilding != null) ? ResponseEntity.ok(updatedBuilding) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an existing building")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated building"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public ResponseEntity<Building> partialUpdateBuilding(
            @Parameter(description = "ID of the building to update") @PathVariable Integer id,
            @Parameter(description = "Building object with fields to update") @RequestBody Building buildingDetails) {
        Building updatedBuilding = buildingService.partialUpdateBuilding(id, buildingDetails);
        return (updatedBuilding != null) ? ResponseEntity.ok(updatedBuilding) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a building")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted building"),
            @ApiResponse(responseCode = "404", description = "Building not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., building is referenced by rooms)")
    })
    public ResponseEntity<Void> deleteBuilding(@Parameter(description = "ID of the building to delete") @PathVariable Integer id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}