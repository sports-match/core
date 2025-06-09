package com.srr.service;

import com.srr.dto.PlayerAnswerDto;
import me.zhengjie.utils.ExecutionResult;
import me.zhengjie.utils.PageResult;
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
     * @return PageResult containing a list of PlayerAnswerDto objects and total count
     */
    PageResult<PlayerAnswerDto> queryAll(PlayerAnswerDto criteria, Pageable pageable);
    
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
     * Overloaded: Submit self-assessment with sport/format
     */
    List<PlayerAnswerDto> submitSelfAssessment(List<PlayerAnswerDto> answers, String sport, String format);
    
    /**
     * Create a new player answer
     * @param resources PlayerAnswerDto object
     * @return Created PlayerAnswerDto
     */
    ExecutionResult create(PlayerAnswerDto resources);
    
    /**
     * Update a player answer
     * @param resources PlayerAnswerDto with updated information
     * @return ExecutionResult containing the updated entity ID
     */
    ExecutionResult update(PlayerAnswerDto resources);

    /**
     * Delete a player answer
     * @param id Player answer ID
     * @return ExecutionResult containing the deleted entity ID
     */
    ExecutionResult delete(Long id);
    
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
