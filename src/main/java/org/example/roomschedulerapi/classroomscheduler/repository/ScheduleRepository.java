package org.example.roomschedulerapi.classroomscheduler.repository;

import org.example.roomschedulerapi.classroomscheduler.model.Schedule;
import org.example.roomschedulerapi.classroomscheduler.model.dto.DetailedScheduleDto; // Make sure to import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Make sure to import
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // Make sure to import
import java.util.List;

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
    @Query("SELECT COUNT(DISTINCT s.room.roomId) FROM Schedule s WHERE s.aClass.shiftEntity.shiftId = :shiftId")
    long countDistinctRoomsUsedOnDateForShift(@Param("shiftId") Long shiftId);

    List<Schedule> findByaClass_Instructor_InstructorId(Long instructorId);
}