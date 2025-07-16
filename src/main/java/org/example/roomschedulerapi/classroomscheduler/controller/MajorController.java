package org.example.roomschedulerapi.classroomscheduler.controller;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.model.ApiResponse;
import org.example.roomschedulerapi.classroomscheduler.model.Major;
import org.example.roomschedulerapi.classroomscheduler.service.MajorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/major")
@RequiredArgsConstructor
public class MajorController {

    private final MajorService majorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Major>>> findAll(){
        List<Major>  major = majorService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(
                "Majors retrieved successfully", major, HttpStatus.OK, LocalDateTime.now()
        ));
    }

}
