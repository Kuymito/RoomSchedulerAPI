// FILE: org/example/mybatisdemo/model/Building.java
package org.example.roomschedulerapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class Building {
    @Schema(description = "Unique identifier of the Building", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer buildingId;

    @Schema(description = "Name of the building", example = "Engineering Hall", required = true)
    private String name;

    @Schema(description = "Short code for the building", example = "EH", required = true)
    private String code;

    public Building() {
    }

    public Building(Integer buildingId, String name, String code) {
        this.buildingId = buildingId;
        this.name = name;
        this.code = code;
    }

    // Getters and Setters
    public Integer getBuildingId() { return buildingId; }
    public void setBuildingId(Integer buildingId) { this.buildingId = buildingId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}