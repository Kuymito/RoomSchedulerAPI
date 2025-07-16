package org.example.roomschedulerapi.classroomscheduler.controller;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.service.ClassArchivalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final ClassArchivalService classArchivalService;

    @PostMapping("/trigger-archival")
    public ResponseEntity<String> triggerArchival() {
        try {
            classArchivalService.archiveOldClasses();
            return ResponseEntity.ok("Archival process triggered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error triggering archival process: " + e.getMessage());
        }
    }
}