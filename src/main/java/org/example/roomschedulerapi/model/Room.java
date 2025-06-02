// FILE: org/example/mybatisdemo/model/Room.java
package org.example.roomschedulerapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class Room {
    @Schema(description = "Unique identifier of the Room", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer roomId;

    @Schema(description = "Building ID where this room is located", example = "10", required = true)
    private Integer buildingId; // FK to buildings(building_id)

    @Schema(description = "Room number", example = "101A", required = true)
    private String number;

    @Schema(description = "Capacity of the room", example = "30", required = true)
    private Integer capacity;

    @Schema(description = "Type of the room (e.g., Lecture Hall, Lab)", example = "Lab", required = true)
    private String type;

    public Room() {
    }

    public Room(Integer roomId, Integer buildingId, String number, Integer capacity, String type) {
        this.roomId = roomId;
        this.buildingId = buildingId;
        this.number = number;
        this.capacity = capacity;
        this.type = type;
    }

    // Getters and Setters
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public Integer getBuildingId() { return buildingId; }
    public void setBuildingId(Integer buildingId) { this.buildingId = buildingId; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}