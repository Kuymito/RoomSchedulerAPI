package org.example.roomschedulerapi.service;// FILE: org/example/mybati.
// .model.ChangeRequest;
import org.example.roomschedulerapi.repository.ChangeRequestRepository;
// import org.example.mybatisdemo.exception.ResourceNotFoundException;
// import org.example.mybatisdemo.repository.InstructorRepo; // For validation
// import org.example.mybatisdemo.repository.AcademicClassRepo; // For validation
// import org.example.mybatisdemo.repository.RoomRepo; // For validation
import org.example.roomschedulerapi.service.ChangeRequestService;
import org.example.roomschedulerapi.model.ChangeRequest;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepo;
    // Inject other repos for FK validation if needed

    public ChangeRequestServiceImpl(ChangeRequestRepository changeRequestRepo) {
        this.changeRequestRepo = changeRequestRepo;
    }

    @Override
    public List<ChangeRequest> getAllChangeRequests() {
        return changeRequestRepo.findAll();
    }

    @Override
    public ChangeRequest getChangeRequestById(Integer requestId) {
        ChangeRequest request = changeRequestRepo.findById(requestId);
        // if (request == null) {
        //     throw new ResourceNotFoundException("ChangeRequest not found with id: " + requestId);
        // }
        return request;
    }

    @Override
    public List<ChangeRequest> getChangeRequestsByInstructorId(Integer instructorId) {
        return changeRequestRepo.findByInstructorId(instructorId);
    }

    @Override
    public List<ChangeRequest> getChangeRequestsByClassId(Integer classId) {
        return changeRequestRepo.findByClassId(classId);
    }

    @Override
    public ChangeRequest createChangeRequest(ChangeRequest changeRequest) {
        // Validate FKs: instructorId, classId, preferredRoomId
        // Set defaults if not provided by client and not handled by DB
        if (changeRequest.getStatus() == null || changeRequest.getStatus().isEmpty()) {
            changeRequest.setStatus("pending");
        }
        if (changeRequest.getRequestedAt() == null) {
            changeRequest.setRequestedAt(Timestamp.from(Instant.now()));
        }
        changeRequestRepo.insert(changeRequest);
        return changeRequest;
    }

    @Override
    public ChangeRequest updateChangeRequest(Integer requestId, ChangeRequest changeRequestDetails) {
        ChangeRequest existingRequest = changeRequestRepo.findById(requestId);
        if (existingRequest == null) {
            // throw new ResourceNotFoundException("ChangeRequest not found with id: " + requestId);
            return null;
        }
        // An instructor should probably not update much other than maybe 'reason' if status is 'pending'.
        // Admin might update status.
        changeRequestDetails.setRequestId(requestId);
        // Ensure requestedAt is not overwritten unless intended
        if (changeRequestDetails.getRequestedAt() == null) {
            changeRequestDetails.setRequestedAt(existingRequest.getRequestedAt());
        }
        changeRequestRepo.update(changeRequestDetails);
        return changeRequestDetails;
    }

    @Override
    public ChangeRequest partialUpdateChangeRequestStatus(Integer requestId, String status) {
        ChangeRequest existingRequest = changeRequestRepo.findById(requestId);
        if (existingRequest == null) {
            // throw new ResourceNotFoundException("ChangeRequest not found with id: " + requestId);
            return null;
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty for update.");
        }
        existingRequest.setStatus(status);
        changeRequestRepo.update(existingRequest); // The update method in repo needs to handle partial if desired, or make a specific one.
        // For simplicity, current repo.update() updates all fields in the object.
        return existingRequest;
    }


    @Override
    public void deleteChangeRequest(Integer requestId) {
        ChangeRequest existingRequest = changeRequestRepo.findById(requestId);
        if (existingRequest == null) {
            // throw new ResourceNotFoundException("ChangeRequest not found with id: " + requestId);
            return;
        }
        // Deleting requests might not be common; usually, they are kept with a final status.
        changeRequestRepo.delete(requestId);
    }

    @Override
    public ChangeRequest partialUpdateChangeRequest(Integer id, ChangeRequest changeRequestDetails) {
        return null;
    }
}