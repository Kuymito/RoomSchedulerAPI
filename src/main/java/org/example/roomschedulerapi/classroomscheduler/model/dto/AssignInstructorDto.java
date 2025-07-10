package org.example.roomschedulerapi.classroomscheduler.model.dto;

public class AssignInstructorDto {

    private Long classId;
    private Long instructorId;
    private String dayOfWeek; // e.g., "MONDAY", "TUESDAY"
    private boolean isOnline;

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

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}