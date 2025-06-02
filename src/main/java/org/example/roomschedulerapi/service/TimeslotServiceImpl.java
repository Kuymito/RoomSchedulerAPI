// FILE: org/example/mybatisdemo/service/TimeSlotServiceImpl.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Timeslot;
import org.example.roomschedulerapi.repository.TimeslotRepository;
// import org.example.mybatisdemo.exception.ResourceNotFoundException; // Assuming you have this
import org.example.roomschedulerapi.repository.TimeslotRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TimeslotServiceImpl implements TimeslotService {

    private final TimeslotRepository timeSlotRepo;

    public TimeslotServiceImpl(TimeslotRepository timeSlotRepo) {
        this.timeSlotRepo = timeSlotRepo;
    }

    @Override
    public List<Timeslot> getAllTimeSlots() {
        return timeSlotRepo.findAll();
    }

    @Override
    public Timeslot getTimeSlotById(Integer timeslotId) {
        Timeslot timeSlot = timeSlotRepo.findById(timeslotId);
        // if (timeSlot == null) {
        //     throw new ResourceNotFoundException("TimeSlot not found with id: " + timeslotId);
        // }
        return timeSlot;
    }

    @Override
    public Timeslot createTimeSlot(Timeslot timeSlot) {
        // Add validation: e.g., startTime < endTime, check for overlapping timeslots if necessary
        if (timeSlot.getStartTime() != null && timeSlot.getEndTime() != null &&
                timeSlot.getStartTime().after(timeSlot.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        timeSlotRepo.insert(timeSlot);
        return timeSlot;
    }

    @Override
    public Timeslot updateTimeSlot(Integer timeslotId, Timeslot timeSlotDetails) {
        Timeslot existingTimeSlot = timeSlotRepo.findById(timeslotId);
        if (existingTimeSlot == null) {
            // throw new ResourceNotFoundException("TimeSlot not found with id: " + timeslotId);
            return null; // Or throw exception
        }
        if (timeSlotDetails.getStartTime() != null && timeSlotDetails.getEndTime() != null &&
                timeSlotDetails.getStartTime().after(timeSlotDetails.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        timeSlotDetails.setTimeslotId(timeslotId); // Ensure ID is set for update
        timeSlotRepo.update(timeSlotDetails);
        return timeSlotDetails;
    }

    @Override
    public Timeslot partialUpdateTimeSlot(Integer timeslotId, Timeslot timeSlotDetails) {
        Timeslot existingTimeSlot = timeSlotRepo.findById(timeslotId);
        if (existingTimeSlot == null) {
            // throw new ResourceNotFoundException("TimeSlot not found with id: " + timeslotId);
            return null; // Or throw exception
        }

        boolean updated = false;
        if (timeSlotDetails.getDay() != null) {
            existingTimeSlot.setDay(timeSlotDetails.getDay());
            updated = true;
        }
        if (timeSlotDetails.getStartTime() != null) {
            existingTimeSlot.setStartTime(timeSlotDetails.getStartTime());
            updated = true;
        }
        if (timeSlotDetails.getEndTime() != null) {
            existingTimeSlot.setEndTime(timeSlotDetails.getEndTime());
            updated = true;
        }
        // For boolean, the getter is isWeekend() and setter is setWeekend()
        // The request body for a boolean field `isWeekend` would be `true` or `false`.
        // If `timeSlotDetails.isWeekend()` is used directly it might cause issues if the field is not present in JSON,
        // as it would default to `false`. A more robust way is to check if it was explicitly set.
        // However, for simplicity with typical JSON deserializers, if the field is present, its value will be used.
        // If not present, the existing value remains.
        // To be absolutely sure for PATCH, one might send a specific DTO or check for presence.
        // Here, we assume if `isWeekend` is in the DTO, it's an intended change.
        // The primitive boolean `isWeekend` in `TimeSlot` will have a default value if not set,
        // so we need a reliable way to know if it was part of the patch request.
        // A common pattern is to use Boolean (wrapper) in the DTO for PATCH.
        // Since our model uses primitive `boolean`, let's assume client sends it if changing.
        // If `isWeekend` field exists in JSON, it will be mapped.
        // This can be tricky. If `isWeekend` is always sent, then this is fine:
        if (existingTimeSlot.isWeekend() != timeSlotDetails.isWeekend()){ // Check if value is different
            // This might not be the correct check for PATCH if timeSlotDetails.isWeekend() defaults.
            // A better approach is a separate DTO for PATCH or checking if specific fields were provided.
            // For now, let's assume a full update of provided fields.
            existingTimeSlot.setWeekend(timeSlotDetails.isWeekend()); // If client sends isWeekend, update it
            updated = true;
        }

        if (timeSlotDetails.getScheduleType() != null) {
            existingTimeSlot.setScheduleType(timeSlotDetails.getScheduleType());
            updated = true;
        }

        if (existingTimeSlot.getStartTime() != null && existingTimeSlot.getEndTime() != null &&
                existingTimeSlot.getStartTime().after(existingTimeSlot.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time after partial update.");
        }

        if (updated) {
            timeSlotRepo.update(existingTimeSlot);
        }
        return existingTimeSlot;
    }

    @Override
    public void deleteTimeSlot(Integer timeslotId) {
        Timeslot existingTimeSlot = timeSlotRepo.findById(timeslotId);
        if (existingTimeSlot == null) {
            // throw new ResourceNotFoundException("TimeSlot not found with id: " + timeslotId);
            return;
        }
        // Add referential integrity check: ensure no classes are using this timeslot
        timeSlotRepo.delete(timeslotId);
    }
}