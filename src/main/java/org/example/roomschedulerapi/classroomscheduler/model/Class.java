package org.example.roomschedulerapi.classroomscheduler.model; // Adjust to your package

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "classes") // Matches the PostgreSQL table name
@AllArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;


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

    public List<ClassInstructor> getClassInstructors() {
        return classInstructors;
    }

    public void setClassInstructors(List<ClassInstructor> classInstructors) {
        this.classInstructors = classInstructors;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
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