package org.example.roomschedulerapi.classroomscheduler.model.dto; // Adjust to your package

// import jakarta.validation.constraints.NotBlank; // Example for validation
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.PositiveOrZero;

public class ClassCreateDto {

    // @NotBlank(message = "Class name cannot be blank")
    private String className; // Renamed from courseName

    // @NotNull(message = "Credits cannot be null")
    // @PositiveOrZero(message = "Credits must be zero or positive")
    private Integer credits; // This field is not in the current Class JPA entity matched to DB

    private String generation;
    private String groupName;
    private String majorName;
    private String degreeName;
    private String facultyName; // This field is not in the current Class JPA entity
    private String semester;    // This field is not in the current Class JPA entity
    private String shift;       // The Class JPA entity expects a Shift object/ID, not a descriptive String

    // Default constructor
    public ClassCreateDto() {
    }

    // Getters and Setters
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
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
}