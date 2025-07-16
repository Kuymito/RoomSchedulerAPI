package org.example.roomschedulerapi.classroomscheduler.repository;

import org.apache.ibatis.annotations.Param;
import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class, Long> {
    /**
     * Counts the number of classes based on their 'is_online' status.
     * @param isOnline The status to count (true for online, false for not).
     * @return The total count of matching classes.
     */
    long countByIsOnline(boolean isOnline);

    /**
     * Counts classes that do not have an entry in the schedules table.
     * This query is more efficient than fetching all IDs and comparing them in memory.
     * @return The count of unassigned classes.
     */
    @Query("SELECT count(c) FROM Class c WHERE c.classId NOT IN (SELECT s.aClass.classId FROM Schedule s)")
    long countUnassignedClasses();


    List<Class> findByIsArchived(boolean isArchived);

    Optional<Class> findByGenerationAndGroupNameAndMajorName(String generation, String groupName, String major);

    boolean existsByClassName(String className);

    boolean existsByDepartmentAndGenerationAndSemester(Department department, String generation, String semester);

    List<Class> findByIsExpired(boolean isExpired);

    @Query("SELECT c FROM Class c WHERE c.isArchived = false AND CAST(c.generation AS integer) <= :archivalYearThreshold")
    List<Class> findClassesEligibleForArchival(@Param("archivalYearThreshold") int archivalYearThreshold);

    List<Class> findByIsArchivedFalse();
}