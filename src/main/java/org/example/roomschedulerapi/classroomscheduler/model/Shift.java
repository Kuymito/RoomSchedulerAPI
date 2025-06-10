package org.example.roomschedulerapi.classroomscheduler.model; // Adjust to your package

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set; // For OneToMany relationship

@Entity
@Table(name = "shifts") // Matches the PostgreSQL table name
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shift_id")
    private Long shiftId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "schedule_type", length = 50)
    private String scheduleType;

    // Optional: If a shift can be associated with many classes and you want to navigate this way
    // Note: In your Class entity, you have 'shiftEntity'. So the mappedBy would be "shiftEntity".
    // @OneToMany(mappedBy = "shiftEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private Set<Class> classesInShift;


    // Constructors
    public Shift() {
    }

    // Getters and Setters
    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    // public Set<Class> getClassesInShift() {
    //     return classesInShift;
    // }

    // public void setClassesInShift(Set<Class> classesInShift) {
    //     this.classesInShift = classesInShift;
    // }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return Objects.equals(shiftId, shift.shiftId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftId);
    }

    @Override
    public String toString() {
        return "Shift{" +
                "shiftId=" + shiftId +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}