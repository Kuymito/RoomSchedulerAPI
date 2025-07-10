package org.example.roomschedulerapi.classroomscheduler.repository;

import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.model.DaysOfWeek;
import org.example.roomschedulerapi.classroomscheduler.model.Schedule;
import org.example.roomschedulerapi.classroomscheduler.model.dto.DetailedScheduleDto; // Make sure to import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Make sure to import
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // Make sure to import
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    boolean existsByaClass_ClassId(Long classId);

    /**
     * Finds all scheduled classes and joins related data to create a detailed view.
     * Uses a DTO projection (constructor expression) to map the results.
     * @return A list of DetailedScheduleDto objects.
     */
//    @Query("SELECT new org.example.roomschedulerapi.classroomscheduler.model.dto.DetailedScheduleDto(" +
//            "s.scheduleId, " +
//            "c.className, " +
//            "r.roomName, " +
//            "r.buildingName, " +
//            "CONCAT(i.firstName, ' ', i.lastName), " +
//            "sh.name, " +
//            "sh.startTime, " +
//            "sh.endTime, " +
//            "s.day, " +
//            "c.groupName" +
//            ") " +
//            "FROM Schedule s " +
//            "JOIN s.aClass c " +
//            "JOIN s.room r " +
//            "JOIN c.shiftEntity sh " +
//            "LEFT JOIN c.instructor i") // LEFT JOIN in case an instructor is not assigned
//    List<DetailedScheduleDto> findAllDetailedSchedules();

    /**
     * Counts the number of distinct rooms that are occupied on a specific date
     * by classes belonging to a specific shift.
     *
     * @param shiftId The ID of the shift to filter by.
     * @return The count of unique rooms used.
     */
    //  s.day = :date AND
    @Query("SELECT COUNT(DISTINCT s.room.roomId) FROM Schedule s WHERE s.aClass.shift.shiftId = :shiftId")
    long countDistinctRoomsUsedOnDateForShift(@Param("shiftId") Long shiftId);

    Optional<Schedule> findByaClass_ClassId(Long classId);

    @Query("SELECT s FROM Schedule s JOIN s.aClass.classInstructors ci WHERE ci.instructor.instructorId = :instructorId")
    List<Schedule> findSchedulesByInstructorId(@Param("instructorId") Long instructorId);

    List<Schedule> findAllByaClass_ClassId(Long classId);

    List<Schedule> findByaClass(org.example.roomschedulerapi.classroomscheduler.model.Class aClass);

    @Query("SELECT s FROM Schedule s " +
            "JOIN s.aClass c " +
            "JOIN c.classInstructors ci " +
            "WHERE s.room.roomId = :roomId " +
            "AND ci.dayOfWeek IN :days " +
            "AND c.shift.startTime = :startTime " +
            "AND c.shift.endTime = :endTime")
    List<Schedule> findConflictingSchedules(
            @Param("roomId") Long roomId,
            @Param("days") List<DaysOfWeek> days,
            @Param("startTime") LocalTime startTime,  // Changed to LocalTime
            @Param("endTime") LocalTime endTime       // Changed to LocalTime
    );

    boolean existsByaClass(Class aClass);
}