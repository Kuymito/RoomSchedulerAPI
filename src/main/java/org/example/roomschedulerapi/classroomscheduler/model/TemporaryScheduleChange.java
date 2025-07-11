package org.example.roomschedulerapi.classroomscheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "temporary_schedule_changes")
@Data
@NoArgsConstructor
public class TemporaryScheduleChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long changeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule originalSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temporary_room_id", nullable = false)
    private Room temporaryRoom;

    @Column(nullable = false)
    private LocalDate effectiveDate; // The specific date for the temporary change

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING; // PENDING, APPROVED, REJECTED

    private String reason;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}