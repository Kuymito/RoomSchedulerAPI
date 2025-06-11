package org.example.roomschedulerapi.classroomscheduler.model.dto; // Adjust to your package

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ClassResponseDto {
    private Long classId;       // Mapped from courseId in your Class entity
    private String className;   // Mapped from courseName in your Class entity
    private String generation;
    private String groupName;   // Mapped from groupName in your Class entity
    private String majorName;   // Mapped from majorName in your Class entity
    private String degreeName;  // Mapped from degreeName in your Class entity
    private String semester;
    private boolean isOnline;
    private boolean isFree;
    private boolean isArchived; // Mapped from is_archived in your Class entity
    private LocalDateTime createdAt;
    private LocalDateTime archivedAt;

    // Representing relationships using their DTOs or key info
    private InstructorResponseDto instructor; // Use the existing InstructorResponseDto
    private DepartmentResponseDto department; // Use the new DepartmentResponseDto
    private ShiftResponseDto shift;       // Use the new ShiftResponseDto

    public ClassResponseDto(Long classId, String className, String generation, String groupName, String majorName, String degreeName, String semester, boolean isOnline, boolean isFree, boolean isArchived, LocalDateTime createdAt, LocalDateTime archivedAt, InstructorResponseDto instructor, DepartmentResponseDto department, ShiftResponseDto shift) {
        this.classId = classId;
        this.className = className;
        this.generation = generation;
        this.groupName = groupName;
        this.majorName = majorName;
        this.degreeName = degreeName;
        this.semester = semester;
        this.isOnline = isOnline;
        this.isFree = isFree;
        this.isArchived = isArchived;
        this.createdAt = createdAt;
        this.archivedAt = archivedAt;
        this.instructor = instructor;
        this.department = department;
        this.shift = shift;
    }

    public ClassResponseDto() {}

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public InstructorResponseDto getInstructor() {
        return instructor;
    }

    public void setInstructor(InstructorResponseDto instructor) {
        this.instructor = instructor;
    }

    public DepartmentResponseDto getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentResponseDto department) {
        this.department = department;
    }

    public ShiftResponseDto getShift() {
        return shift;
    }

    public void setShift(ShiftResponseDto shift) {
        this.shift = shift;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}