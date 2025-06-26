package org.example.roomschedulerapi.classroomscheduler.model.dto; // Adjust to your package

// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructorCreateDto {

    // @NotBlank(message = "First name is mandatory")
    // @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    // @NotBlank(message = "Last name is mandatory")
    // @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    // @NotBlank(message = "Email is mandatory")
    // @Email(message = "Email should be valid")
    private String email;

    // @NotBlank(message = "Password is mandatory")
    // @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    // @NotNull(message = "Role ID is mandatory")
    private Long roleId;

    // @NotNull(message = "Department ID is mandatory")
    private Long departmentId;

    private String phone;
    private String degree;
    private String major;
    private String profile;
    private String address;

    public InstructorCreateDto(String firstName, String lastName, String email, String password, Long roleId, Long departmentId, String phone, String degree, String major, String profile, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
        this.departmentId = departmentId;
        this.phone = phone;
        this.degree = degree;
        this.major = major;
        this.profile = profile;
        this.address = address;
    }

    public InstructorCreateDto() {}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}