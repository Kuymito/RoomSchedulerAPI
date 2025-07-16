package org.example.roomschedulerapi.classroomscheduler.model; // Adjust to your package

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "classes") // Matches the PostgreSQL table name
@AllArgsConstructor
@Data
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long classId;

    @Column(name = "name", nullable = false, length = 255)
    private String className;

    @OneToMany(mappedBy = "aClass", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClassInstructor> classInstructors;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "generation", length = 50)
    private String generation;

    @Column(name = "\"group\"", length = 50) // Escaped "group" as it is a reserved keyword
    private String groupName;

    @Column(name = "major", length = 100)
    private String majorName;

    @Column(name = "degree", length = 100)
    private String degreeName;

    @Column(name = "is_online", columnDefinition = "boolean default false")
    private boolean isOnline;

    @Column(name = "is_free", columnDefinition = "boolean default false")
    private boolean isFree;

    @Column(name = "is_archived", columnDefinition = "boolean default false")
    private boolean isArchived;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "created_at", columnDefinition = "timestamp with time zone default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "semester", length = 100)
    private String semester;

    @Column(name = "year")
    private Integer year;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(name = "is_expired")
    private Boolean isExpired;


    public Class() {
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class aClass = (Class) o;
        return Objects.equals(classId, aClass.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classId);
    }
}