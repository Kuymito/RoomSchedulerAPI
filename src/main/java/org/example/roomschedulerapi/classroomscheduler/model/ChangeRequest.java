package org.example.roomschedulerapi.classroomscheduler.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "change_requests")
@Getter
@Setter
@NoArgsConstructor
public class ChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Class aClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING) // This stores the status as a clean string ("PENDING", "APPROVED") in the DB
    @Column(name = "status") // It's best practice to rename the column from is_approved to status
    private RequestStatus status;

    @Column(name = "requested_at")
    private OffsetDateTime requestedAt;

    @Column(name = "archived")
    private boolean archived = false;

    @Column(name = "expired")
    private boolean expired = false;

    @Column(name = "day_of_change")
    private LocalDateTime dayOfChange;

    // Add these setter methods
    public void setRoom(Room room) {
        this.room = room;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getDayOfChange() {
        return dayOfChange;
    }

    public void setDayOfChange(LocalDateTime dayOfChange) {
        this.dayOfChange = dayOfChange;
    }
}