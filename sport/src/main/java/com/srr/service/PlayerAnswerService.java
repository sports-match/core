package com.srr.service;

import com.srr.dto.PlayerAnswerDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
* @description PlayerAnswer service interface
* @author Chanheng
* @date 2025-05-31
**/
public interface PlayerAnswerService {

    /**
     * Get all player answers
     * @param criteria Filter criteria
     * @param pageable Pagination information
     * @return Map containing a list of PlayerAnswerDto objects and total count
     */
    Map<String, Object> queryAll(PlayerAnswerDto criteria, Pageable pageable);
    
    /**
     * Get all answers for a player
     * @param playerId The player ID
     * @return List of player answers
     */
    List<PlayerAnswerDto> getByPlayerId(Long playerId);
    
    /**
     * Get answers by player ID and question category
     * @param playerId The player ID
     * @param category The question category
     * @return List of player answers for the given category
     */
    List<PlayerAnswerDto> getByPlayerIdAndCategory(Long playerId, String category);
    
    /**
     * Submit player self-assessment answers
     * @param answers List of player answer DTOs
     * @return List of saved player answer DTOs
     */
    List<PlayerAnswerDto> submitSelfAssessment(List<PlayerAnswerDto> answers);
    
    /**
     * Create a new player answer
     * @param resources PlayerAnswerDto object
     * @return Created PlayerAnswerDto
     */
    PlayerAnswerDto create(PlayerAnswerDto resources);
    
    /**
     * Update a player answer
     * @param resources PlayerAnswerDto object with updated information
     */
    void update(PlayerAnswerDto resources);
    
    /**
     * Delete a player answer by ID
     * @param id PlayerAnswer ID
     */
    void delete(Long id);
    
    /**
     * Get a player answer by ID
     * @param id PlayerAnswer ID
     * @return PlayerAnswerDto object
     */
    PlayerAnswerDto findById(Long id);
    
    /**
     * Check if a player has completed self-assessment
     * @param playerId The player ID
     * @return true if player has completed assessment, false otherwise
     */
    boolean hasCompletedSelfAssessment(Long playerId);
}
