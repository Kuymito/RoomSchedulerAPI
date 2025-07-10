package org.example.roomschedulerapi.classroomscheduler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to be thrown when an attempt is made to schedule an instructor
 * at a time when they are already assigned to another class.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Sets the HTTP status to 409 Conflict
public class InstructorConflictException extends RuntimeException {

    public InstructorConflictException(String message) {
        super(message);
    }
}
