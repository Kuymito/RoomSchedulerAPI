package org.example.roomschedulerapi.classroomscheduler.service.impl;

import org.example.roomschedulerapi.classroomscheduler.model.Major;
import org.example.roomschedulerapi.classroomscheduler.repository.MajorRepository;
import org.example.roomschedulerapi.classroomscheduler.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorServiceImpl implements MajorService {

    private MajorRepository majorRepository;

    @Autowired
    public void setMajorRepository(MajorRepository majorRepository) {
        this.majorRepository = majorRepository;
    }

    @Override
    public List<Major> findAll(){
        return majorRepository.findAll();
    }
}
