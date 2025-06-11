package org.example.roomschedulerapi.classroomscheduler.model.dto; // Adjust to your package

// import jakarta.validation.constraints.PositiveOrZero;

public class ClassUpdateDto {

    private String className; // Renamed from courseName

    // @PositiveOrZero(message = "Credits must be zero or positive")

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

    // Getters and Setters
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

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Boolean getIs_archived() {
        return is_archived;
    }

    public void setIs_archived(Boolean is_archived) {
        this.is_archived = is_archived;
    }
}