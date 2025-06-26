package org.example.roomschedulerapi.classroomscheduler.model.dto; // Adjust to your package

// import jakarta.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassUpdateDto {

    // Getters and Setters
    private String className; // Renamed from courseName

    // @PositiveOrZero(message = "Credits must be zero or positive")
    private String day;

    private String generation;
    private String groupName;
    private String majorName;
    private String degreeName;
    private String facultyName; // This field is not in the current Class JPA entity
    private String semester;    // This field is not in the current Class JPA entity
    private String shift;       // The Class JPA entity expects a Shift object/ID

    private Boolean is_archived; // For PATCHing the archive status

    // Default constructor
    public ClassUpdateDto() {
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public void setIs_archived(Boolean is_archived) {
        this.is_archived = is_archived;
    }

    public void setDay(String day) {}
}