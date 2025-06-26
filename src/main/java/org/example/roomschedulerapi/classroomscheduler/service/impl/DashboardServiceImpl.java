package org.example.roomschedulerapi.classroomscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.service.DashboardService;
import org.example.roomschedulerapi.classroomscheduler.model.dto.DashboardResponseDto;
import org.example.roomschedulerapi.classroomscheduler.repository.ChangeRequestRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.ClassRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.RoomRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;
    private final ChangeRequestRepository changeRequestRepository;
    private final RoomRepository roomRepository;

    private static final Long DEFAULT_SHIFT_ID = 1L; // Corresponds to '07:00 - 10:00' shift

    @Override
    public DashboardResponseDto getDashboardData(Long shiftId) {
        if (shiftId == null) {
            shiftId = DEFAULT_SHIFT_ID;
        }

        long classAssignCount = scheduleRepository.count();
        long unassignedClassCount = classRepository.countUnassignedClasses();
        long onlineClassCount = classRepository.countByIsOnline(true);
        long expiredCount = changeRequestRepository.countByExpired(true);

        Map<String, Long> roomAvailability = calculateWeeklyRoomAvailability(shiftId);

        return new DashboardResponseDto(
                classAssignCount,
                expiredCount,
                unassignedClassCount,
                onlineClassCount,
                roomAvailability
        );
    }

    private Map<String, Long> calculateWeeklyRoomAvailability(Long shiftId) {
        Map<String, Long> availabilityMap = new LinkedHashMap<>();
        long totalRooms = roomRepository.count();

        // Let's calculate for the upcoming week starting from next Monday
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = nextMonday.plusDays(i);
            long usedRooms = scheduleRepository.countDistinctRoomsUsedOnDateForShift( shiftId);
            long availableRooms = totalRooms - usedRooms;

            // Get the 3-letter day name (e.g., "Mon")
            String dayName = currentDay.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            availabilityMap.put(dayName, availableRooms);
        }

        return availabilityMap;
    }
}