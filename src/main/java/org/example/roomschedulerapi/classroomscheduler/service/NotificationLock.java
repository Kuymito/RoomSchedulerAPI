package org.example.roomschedulerapi.classroomscheduler.service;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A thread-safe service to prevent duplicate notification creation during race conditions.
 * It provides a simple locking mechanism based on the change request ID.
 */
@Component
public class NotificationLock {

    // A thread-safe map to store active locks. The key is the change request ID.
    private final ConcurrentMap<Long, Object> locks = new ConcurrentHashMap<>();

    /**
     * Attempts to acquire a lock for a given change request ID.
     * @param changeRequestId The ID of the change request.
     * @return true if the lock was successfully acquired, false if it was already held.
     */
    public boolean acquire(Long changeRequestId) {
        // putIfAbsent is an atomic operation. It will only add the key if it's not already present.
        // It returns null if the key was added, or the existing value if the key was already there.
        return locks.putIfAbsent(changeRequestId, new Object()) == null;
    }

    /**
     * Releases the lock for a given change request ID.
     * @param changeRequestId The ID of the change request.
     */
    public void release(Long changeRequestId) {
        locks.remove(changeRequestId);
    }
}