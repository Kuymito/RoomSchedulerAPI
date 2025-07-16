package org.example.roomschedulerapi.classroomscheduler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "archival")
@Data
public class ClassArchivalConfig {

    private int baseYear;
    private int baseGeneration;
    private int defaultYearsUntilArchival = 5;
    private Map<String, Integer> yearsUntilArchivalByDegree = new HashMap<>();

}