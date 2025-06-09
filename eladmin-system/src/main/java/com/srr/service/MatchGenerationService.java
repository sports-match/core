package com.srr.service;

import com.srr.domain.MatchGroup;

/**
 * Service for generating matches for match groups
 */
public interface MatchGenerationService {
    
    /**
     * Generate all possible matches between teams in a match group
     * 
     * @param matchGroup The match group to generate matches for
     * @return The number of matches generated
     */
    int generateMatchesForGroup(MatchGroup matchGroup);
}
