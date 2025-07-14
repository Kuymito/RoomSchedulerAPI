package org.example.roomschedulerapi.classroomscheduler.repository;

import org.apache.ibatis.annotations.Param;
import org.example.roomschedulerapi.classroomscheduler.model.ChangeRequest; // Assuming you have this model
import org.example.roomschedulerapi.classroomscheduler.model.Schedule;
import org.example.roomschedulerapi.classroomscheduler.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long> {

    @Query("SELECT cr FROM ChangeRequest cr " +
            "WHERE cr.originalSchedule.id = :scheduleId " +
            "AND cr.effectiveDate = :date " +
            "AND cr.status = 'APPROVED'")
    Optional<ChangeRequest> findApprovedChangeForDate(Long scheduleId, LocalDate date);

    @Query("SELECT COUNT(cr) FROM ChangeRequest cr WHERE cr.effectiveDate < :currentDate")
    long countExpiredRequests(@Param("currentDate") LocalDate currentDate);

    List<ChangeRequest> findAllByEffectiveDateAndStatus(LocalDate today, RequestStatus requestStatus);

    List<ChangeRequest> findAllByOriginalScheduleInAndStatus(List<Schedule> permanentSchedules, RequestStatus requestStatus);

    List<ChangeRequest> findAllByStatus(RequestStatus requestStatus);

    void deleteByEffectiveDateBefore(LocalDate yesterday);

    void deleteAllByOriginalSchedule_ScheduleId(Long scheduleId);

    @Query("SELECT cr FROM ChangeRequest cr WHERE cr.originalSchedule IN :schedules AND cr.status = 'APPROVED' AND cr.effectiveDate >= :currentDate")
    List<ChangeRequest> findActiveApprovedChangesForSchedules(@Param("schedules") List<Schedule> schedules, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT cr FROM ChangeRequest cr WHERE cr.status = 'APPROVED' AND cr.effectiveDate >= :currentDate")
    List<ChangeRequest> findAllActiveApprovedChanges(@Param("currentDate") LocalDate currentDate);
}