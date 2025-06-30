package org.example.roomschedulerapi.classroomscheduler.model.dto;

public class AssignInstructorDto {

    private Long classId;
    private Long instructorId;

    public AssignInstructorDto(Long classId, Long instructorId) {
        this.classId = classId;
        this.instructorId = instructorId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }
}