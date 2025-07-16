package org.example.roomschedulerapi.classroomscheduler.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "rooms")
@Data
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "building_name", nullable = false)
    private String buildingName;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "equipment")
    private String equipment;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "type")
    private String type;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomAvailability> availability;

    // Getters and Setters
}