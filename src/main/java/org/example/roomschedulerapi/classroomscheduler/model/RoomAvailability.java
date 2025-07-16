package org.example.roomschedulerapi.classroomscheduler.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "room_availability")
@Data
public class RoomAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @Column(name = "is_available")
    private Boolean isAvailable;

    // Getters and Setters
}