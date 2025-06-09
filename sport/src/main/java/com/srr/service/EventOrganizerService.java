package com.srr.service;

import com.srr.domain.EventOrganizer;
import com.srr.enumeration.VerificationStatus;
import me.zhengjie.utils.ExecutionResult;

import java.util.List;

/**
 * Service for managing event organizers
 */
public interface EventOrganizerService {
    
    /**
     * Create a new event organizer
     * @param resources The event organizer to create
     * @return Execution result with the created entity's ID
     */
    ExecutionResult create(EventOrganizer resources);
    
    /**
     * Find event organizers by user ID
     * @param userId The user ID to search for
     * @return List of event organizers associated with the user
     */
    List<EventOrganizer> findByUserId(Long userId);

    /**
     * Update the verification status of an event organizer.
     * @param organizerId The ID of the event organizer to update.
     * @param status The new verification status.
     * @return ExecutionResult containing the updated entity's ID.
     */
    ExecutionResult updateVerificationStatus(Long organizerId, VerificationStatus status);
}
