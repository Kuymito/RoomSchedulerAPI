// FILE: org/example/mybatisdemo/service/ChangeRequestService.java
package org.example.roomschedulerapi.service;

import org.example.roomschedulerapi.model.ChangeRequest;
import java.util.List;

public interface ChangeRequestService {
    List<ChangeRequest> getAllChangeRequests();
    ChangeRequest getChangeRequestById(Integer requestId);
    List<ChangeRequest> getChangeRequestsByInstructorId(Integer instructorId);
    List<ChangeRequest> getChangeRequestsByClassId(Integer classId);
    ChangeRequest createChangeRequest(ChangeRequest changeRequest);
    ChangeRequest updateChangeRequest(Integer requestId, ChangeRequest changeRequestDetails); // Full update or admin update
    ChangeRequest partialUpdateChangeRequestStatus(Integer requestId, String status); // Specific for status
    void deleteChangeRequest(Integer requestId);

    ChangeRequest partialUpdateChangeRequest(Integer id, ChangeRequest changeRequestDetails);
}