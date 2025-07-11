package org.example.roomschedulerapi.classroomscheduler.service;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.repository.ChangeRequestRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CleanupService {

    private final ChangeRequestRepository changeRequestRepository;

    /**
     * This method runs automatically every day at 2 AM.
     * It finds and deletes all change requests whose effectiveDate is in the past.
     */
    @Transactional
    @Scheduled(cron = "0 0 2 * * ?") // Runs at 2:00 AM daily
    public void cleanupExpiredChangeRequests() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        changeRequestRepository.deleteByEffectiveDateBefore(yesterday);
        System.out.println("Cleaned up expired change requests.");
    }
}