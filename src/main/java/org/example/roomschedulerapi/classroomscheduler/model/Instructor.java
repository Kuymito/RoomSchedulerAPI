package org.example.roomschedulerapi.classroomscheduler.model; // Adjust to your package

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "instructor") // Matches the PostgreSQL table name
public class Instructor implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instructor_id")
    private Long instructorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    // For potentially long text
    @Column(name = "profile")
    private String profile;

    @Column(name = "password", nullable = false, length = 255)
    private String password; // Store hashed passwords

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "degree", length = 100)
    private String degree;

    @Column(name = "major", length = 100)
    private String major;

    // For potentially long text
    @Column(name = "address")
    private String address;

    @Column(name = "is_archived", nullable = false)
    private boolean isArchived = false;


    // Optional: If an instructor can teach many classes and you want to navigate this way
    // @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private Set<Class> classesTaught;


    // Constructors
    public Instructor() {
    }



    // public Set<Class> getClassesTaught() {
    //     return classesTaught;
    // }

    // public void setClassesTaught(Set<Class> classesTaught) {
    //     this.classesTaught = classesTaught;
    // }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // The authority should be the role name, e.g., "ROLE_INSTRUCTOR", "ROLE_ADMIN"
        return List.of(new SimpleGrantedAuthority(role.getRoleName()));
    }

    @Override
    public String getUsername() {
        // Use email as the username for authentication
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !isArchived; // An archived user is disabled
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructor that = (Instructor) o;
        return Objects.equals(instructorId, that.instructorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructorId);
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "instructorId=" + instructorId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}