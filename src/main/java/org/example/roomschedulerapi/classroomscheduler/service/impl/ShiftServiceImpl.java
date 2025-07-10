package org.example.roomschedulerapi.classroomscheduler.service.impl;

import org.example.roomschedulerapi.classroomscheduler.model.Shift;
import org.example.roomschedulerapi.classroomscheduler.repository.ShiftRepository;
import org.example.roomschedulerapi.classroomscheduler.service.ShiftService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShiftServiceImpl implements ShiftService {

    private ShiftRepository shiftRepository;

    public ShiftServiceImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

}
