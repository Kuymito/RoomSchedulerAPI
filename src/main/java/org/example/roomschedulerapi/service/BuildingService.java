// FILE: org/example/mybatisdemo/service/BuildingService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Building;
import java.util.List;

public interface BuildingService {
    List<Building> getAllBuildings();
    Building getBuildingById(Integer buildingId);
    Building createBuilding(Building building);
    Building updateBuilding(Integer buildingId, Building buildingDetails);
    Building partialUpdateBuilding(Integer buildingId, Building buildingDetails);
    void deleteBuilding(Integer buildingId);
}