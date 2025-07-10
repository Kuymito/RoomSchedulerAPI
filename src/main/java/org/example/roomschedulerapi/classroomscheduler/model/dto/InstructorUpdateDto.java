package org.example.roomschedulerapi.classroomscheduler.model.dto; // Adjust to your package

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructorUpdateDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String degree;
    private String major;
    private String address;
    private Long departmentId;
    private String profile;


    public InstructorUpdateDto(String firstName, String lastName, String email, String phone, String degree, String major, String address, Long departmentId, String profile) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.degree = degree;
        this.major = major;
        this.address = address;
        this.departmentId = departmentId;
        this.profile = profile;
    }

    public InstructorUpdateDto() {}

    // Getters and Setters are handled by Lombok, but included here for clarity if you're not using it.
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
}