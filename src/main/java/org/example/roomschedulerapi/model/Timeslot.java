// FILE: org/example/mybatisdemo/model/TimeSlot.java
package org.example.roomschedulerapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.Time; // Use java.sql.Time for direct mapping to SQL TIME type

public class Timeslot {
    @Schema(description = "Unique identifier of the TimeSlot", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer timeslotId;

    @Schema(description = "Day of the week", example = "Monday", required = true)
    private String day;

    @Schema(description = "Start time of the slot", example = "09:00:00", required = true, type = "string", format = "time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time startTime;

    @Schema(description = "End time of the slot", example = "10:30:00", required = true, type = "string", format = "time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time endTime;

    @Schema(description = "Indicates if the slot is on a weekend", example = "false", required = true)
    private boolean isWeekend; // boolean because NOT NULL in SQL

    @Schema(description = "Type of schedule (e.g., Regular, Exam)", example = "Regular", required = true)
    private String scheduleType;

    public Timeslot() {
    }

    public Timeslot(Integer timeslotId, String day, Time startTime, Time endTime, boolean isWeekend, String scheduleType) {
        this.timeslotId = timeslotId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWeekend = isWeekend;
        this.scheduleType = scheduleType;
    }

    // Getters and Setters
    public Integer getTimeslotId() { return timeslotId; }
    public void setTimeslotId(Integer timeslotId) { this.timeslotId = timeslotId; }
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public boolean isWeekend() { return isWeekend; } // Getter for boolean
    public void setWeekend(boolean weekend) { isWeekend = weekend; } // Setter for boolean
    public String getScheduleType() { return scheduleType; }
    public void setScheduleType(String scheduleType) { this.scheduleType = scheduleType; }
}