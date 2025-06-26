package org.example.roomschedulerapi.classroomscheduler.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "schedules", uniqueConstraints = {
        // This constraint ensures a class can only be assigned to one room.
        // Remove or adjust if a class can be in multiple rooms.
        @UniqueConstraint(columnNames = {"class_id"})
})
@Getter @Setter @NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @OneToOne(fetch = FetchType.LAZY) // Changed to OneToOne as a class is now in one room
    @JoinColumn(name = "class_id", nullable = false, unique = true)
    private Class aClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}