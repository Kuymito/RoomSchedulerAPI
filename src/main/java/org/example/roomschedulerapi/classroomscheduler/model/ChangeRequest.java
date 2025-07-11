package org.example.roomschedulerapi.classroomscheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "change_requests")
@Data // Use @Data for getters, setters, toString, etc.
@NoArgsConstructor
public class ChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule originalSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor requestingInstructor;

    @OneToMany(
            mappedBy = "changeRequest",
            cascade = CascadeType.ALL, // Ensures operations (like delete) are cascaded to notifications
            orphanRemoval = true       // Removes notifications if they are disconnected from a change request
    )
    private List<Notification> notifications;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temporary_room_id", nullable = false)
    private Room temporaryRoom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate; // The specific date the change is for

    @Column(name = "requested_at")
    private OffsetDateTime requestedAt;

    @Column(name = "is_archived", nullable = false)
    private boolean archived = false;

    @Override
    public String toString() {
        return "ChangeRequest{" +
                "id=" + id +
                ", status=" + status +
                // Only include IDs of related entities, not the full objects
                ", requestingInstructorId=" + (requestingInstructor != null ? requestingInstructor.getInstructorId() : null) +
                ", originalScheduleId=" + (originalSchedule != null ? originalSchedule.getScheduleId() : null) +
                '}';
    }

}