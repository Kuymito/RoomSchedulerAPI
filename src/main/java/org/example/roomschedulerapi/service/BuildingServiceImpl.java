// FILE: org/example/mybatisdemo/service/BuildingServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Building;
import org.example.roomschedulerapi.repository.BuildingRepository;
// import org.example.mybatisdemo.exception.ResourceNotFoundException;
import org.example.roomschedulerapi.repository.BuildingRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepo;

    public BuildingServiceImpl(BuildingRepository buildingRepo) {
        this.buildingRepo = buildingRepo;
    }

    @Override
    public List<Building> getAllBuildings() {
        return buildingRepo.findAll();
    }

    @Override
    public Building getBuildingById(Integer buildingId) {
        Building building = buildingRepo.findById(buildingId);
        // if (building == null) {
        //     throw new ResourceNotFoundException("Building not found with id: " + buildingId);
        // }
        return building;
    }

    @Override
    public Building createBuilding(Building building) {
        // Add validation (e.g., unique name/code)
        buildingRepo.insert(building);
        return building;
    }

    @Override
    public Building updateBuilding(Integer buildingId, Building buildingDetails) {
        Building existingBuilding = buildingRepo.findById(buildingId);
        if (existingBuilding == null) {
            // throw new ResourceNotFoundException("Building not found with id: " + buildingId);
            return null;
        }
        buildingDetails.setBuildingId(buildingId);
        buildingRepo.update(buildingDetails);
        return buildingDetails;
    }

    @Override
    public Building partialUpdateBuilding(Integer buildingId, Building buildingDetails) {
        Building existingBuilding = buildingRepo.findById(buildingId);
        if (existingBuilding == null) {
            // throw new ResourceNotFoundException("Building not found with id: " + buildingId);
            return null;
        }

        boolean updated = false;
        if (buildingDetails.getName() != null) {
            existingBuilding.setName(buildingDetails.getName());
            updated = true;
        }
        if (buildingDetails.getCode() != null) {
            existingBuilding.setCode(buildingDetails.getCode());
            updated = true;
        }

        if (updated) {
            buildingRepo.update(existingBuilding);
        }
        return existingBuilding;
    }

    @Override
    public void deleteBuilding(Integer buildingId) {
        Building existingBuilding = buildingRepo.findById(buildingId);
        if (existingBuilding == null) {
            // throw new ResourceNotFoundException("Building not found with id: " + buildingId);
            return;
        }
        // Consider referential integrity: what happens if rooms reference this building?
        buildingRepo.delete(buildingId);
    }
}