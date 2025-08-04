package org.example.roomschedulerapi.classroomscheduler.model.dto;

// You can place this in an appropriate package, e.g., org.example.yourproject.model.dto

public class InstructorArchiveRequestDto {

    private Long instructorId;
    private boolean setarchive; // Matches the JSON key and MyBatis parameter

    // Default constructor (often needed for JSON deserialization libraries like Jackson)
    public InstructorArchiveRequestDto() {
    }

    // Getters and Setters
    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public boolean isSetarchive() { // Standard getter for boolean primitive
        return setarchive;
    }

    public void setSetarchive(boolean setarchive) {
        this.setarchive = setarchive;
    }

    // Optional: toString() for debugging
    @Override
    public String toString() {
        return "InstructorArchiveRequestDto{" +
                "instructorId=" + instructorId +
                ", setarchive=" + setarchive +
                '}';
    }
}
