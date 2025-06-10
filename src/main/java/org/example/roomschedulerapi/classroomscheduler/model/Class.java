package org.example.roomschedulerapi.classroomscheduler.model; // Adjust to your package

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

// TODO: Consider renaming this entity from "Class" to avoid conflicts with java.lang.Class.
// For example: Course, ScheduledClass, Clazz, etc.
@Entity
@Table(name = "classes") // Matches the PostgreSQL table name
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id") // Mapped to class_id in the database
    private Long classId; // Java field name can remain courseId or change to classId

    @Column(name = "name", nullable = false, length = 255) // Mapped to name in the database
    private String className;

    // 'instructor_id' - Mapped via @ManyToOne Instructor instructor
    @ManyToOne(fetch = FetchType.LAZY) // LAZY is often a good default
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // 'shift_id' - Mapped via @ManyToOne Shift shiftEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shiftEntity; // Renamed to avoid potential conflict if 'shift' string was needed

    @Column(name = "generation", length = 50)
    private String generation;

    @Column(name = "\"group\"", length = 50) // DB column "group" (quoted as it's a keyword)
    private String groupName; // Java field name

    @Column(name = "major", length = 100) // Mapped to major in the database
    private String majorName;

    @Column(name = "degree", length = 100) // Mapped to degree in the database
    private String degreeName;

    @Column(name = "is_online") // Defaults to false in DB
    private boolean isOnline = false;

    @Column(name = "is_free") // Defaults to false in DB
    private boolean isFree = false;

    @Column(name = "is_archived", nullable = false) // nullable = false to match DB default behavior
    private boolean is_archived = false;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // Removed 'updatedAt' as it was not in the provided CLASSES DDL.
    // If you add an 'updated_at' column to your DB, you can re-add this field and @PreUpdate.

    // JPA life cycle callback for createdAt timestamp
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // If you had an updatedAt field for persistence:
        // updatedAt = LocalDateTime.now();
    }

    // Removed @PreUpdate as 'updatedAt' field was removed.
    // If 'updatedAt' is re-added and persisted:
    // @PreUpdate
    // protected void onUpdate() {
    //     updatedAt = LocalDateTime.now();
    // }

    // Constructors
    public Class() {
    }

    // Getters and Setters

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Shift getShiftEntity() {
        return shiftEntity;
    }

    public void setShiftEntity(Shift shiftEntity) {
        this.shiftEntity = shiftEntity;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public boolean isIs_archived() { // Kept as per your original naming
        return is_archived;
    }

    public void setIs_archived(boolean is_archived) {
        this.is_archived = is_archived;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    // toString() method can be useful for debugging
    @Override
    public String toString() {
        return "Class{" +
                "courseId=" + classId +
                ", courseName='" + className + '\'' +
                ", generation='" + generation + '\'' +
                // Add other fields as needed, be careful with LAZY loaded entities
                '}';
    }
}