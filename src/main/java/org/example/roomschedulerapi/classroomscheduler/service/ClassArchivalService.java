package org.example.roomschedulerapi.classroomscheduler.service;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.config.ClassArchivalConfig;
import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.repository.ClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassArchivalService {

    private static final Logger logger = LoggerFactory.getLogger(ClassArchivalService.class);
    private final ClassRepository classRepository;
    private final ClassArchivalConfig archivalConfig;

    /**
     * This scheduled task runs once every day at midnight (server time)
     * to check for classes that should be archived.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void archiveOldClasses() {
        logger.info("Starting scheduled task to check for old classes...");

        int currentYear = Year.now().getValue();
        List<Class> activeClasses = classRepository.findByIsArchivedFalse();

        if (activeClasses.isEmpty()) {
            logger.info("No active classes found to check for archival.");
            return;
        }

        logger.info("Found {} active classes to evaluate for archival.", activeClasses.size());
        int totalArchivedCount = 0;

        for (Class cls : activeClasses) {
            try {
                int generationNumber = Integer.parseInt(cls.getGeneration());

                // Calculate the actual calendar year the class started
                int classYear = archivalConfig.getBaseYear() + (generationNumber - archivalConfig.getBaseGeneration());

                // Determine the archival rule for the class's degree
                int yearsUntilArchival = archivalConfig.getYearsUntilArchivalByDegree()
                        .getOrDefault(cls.getMajorName(), archivalConfig.getDefaultYearsUntilArchival());

                // Calculate when the class is due to be archived and how many years are left
                int archivalYear = classYear + yearsUntilArchival;
                int yearsLeft = archivalYear - currentYear;

                // Log the status for every class checked
                if (yearsLeft > 0) {
                    logger.info("Checked Class '{}': ID: {}, Generation: {}. {} years left until archival.",
                            cls.getClassName(), cls.getClassId(), cls.getGeneration(), yearsLeft);
                } else {
                    cls.setArchived(true);
                    logger.warn("ARCHIVING Class '{}': ID: {}, Generation: {}. Met archival threshold of {} years.",
                            cls.getClassName(), cls.getClassId(), cls.getGeneration(), yearsUntilArchival);
                    totalArchivedCount++;
                }

            } catch (NumberFormatException e) {
                logger.warn("Could not parse generation for class ID {}: '{}'. Skipping archival check.", cls.getClassId(), cls.getGeneration());
            }
        }

        if (totalArchivedCount > 0) {
            // Save only the classes that have been marked for archival
            classRepository.saveAll(activeClasses.stream().filter(Class::isArchived).collect(Collectors.toList()));
            logger.info("Successfully archived a total of {} classes in this run.", totalArchivedCount);
        } else {
            logger.info("No classes were eligible for archival in this run.");
        }
    }
}