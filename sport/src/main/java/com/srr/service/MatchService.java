package com.srr.service;

import com.srr.domain.Match;
import com.srr.dto.MatchScoreUpdateDto;

import java.util.List;

/**
 * Service for match operations
 */
public interface MatchService {
    
    /**
     * Update the score of a match
     * 
     * @param scoreUpdateDto DTO containing match ID and scores
     * @return The updated match
     */
    Match updateMatchScore(MatchScoreUpdateDto scoreUpdateDto);
    
    /**
     * Find a match by its ID
     * 
     * @param id Match ID
     * @return The match if found
     */
    Match findById(Long id);
    
    /**
     * Verify a match score as an admin
     * 
     * @param matchId ID of the match to verify
     * @return The updated match
     */
    Match verifyMatchScore(Long matchId);
    
    /**
     * Find all matches for the current user (matches where the user is part of team A or team B)
     * 
     * @return List of matches involving the current user
     */
    List<Match> findMatchesForCurrentUser();
}
