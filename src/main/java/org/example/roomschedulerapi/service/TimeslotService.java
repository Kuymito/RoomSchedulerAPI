// FILE: org/example/mybatisdemo/service/TimeSlotService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.Timeslot;
import java.util.List;

public interface TimeslotService {
    List<Timeslot> getAllTimeSlots();
    Timeslot getTimeSlotById(Integer timeslotId);
    Timeslot createTimeSlot(Timeslot timeSlot);
    Timeslot updateTimeSlot(Integer timeslotId, Timeslot timeSlotDetails);
    Timeslot partialUpdateTimeSlot(Integer timeslotId, Timeslot timeSlotDetails);
    void deleteTimeSlot(Integer timeslotId);
}