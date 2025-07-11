package org.example.roomschedulerapi.classroomscheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A notification can belong to an Instructor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id") // Nullable
    private Instructor instructor;

    // OR it can belong to an Admin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id") // Nullable
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_request_id", nullable = false)
    private ChangeRequest changeRequest;

    @Column(nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                // Only include IDs of related entities
                ", changeRequestId=" + (changeRequest != null ? changeRequest.getId() : null) +
                '}';
    }
}