/*
*  Copyright 2019-2025 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package com.srr.repository;

import com.srr.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
* @website https://eladmin.vip
* @author Chanheng
* @date 2025-05-25
**/
public interface MatchRepository extends JpaRepository<Match, Long>, JpaSpecificationExecutor<Match> {
    
    /**
     * Delete all matches associated with a specific match group
     * @param matchGroupId ID of the match group
     */
    @Modifying
    @Query("DELETE FROM Match m WHERE m.matchGroup.id = :matchGroupId")
    void deleteByMatchGroupId(@Param("matchGroupId") Long matchGroupId);
    
    /**
     * Find all matches for a specific match group, ordered by match order
     * @param matchGroupId ID of the match group
     * @return List of ordered matches
     */
    @Query("SELECT m FROM Match m WHERE m.matchGroup.id = :matchGroupId ORDER BY m.matchOrder ASC")
    List<Match> findByMatchGroupIdOrderByMatchOrder(@Param("matchGroupId") Long matchGroupId);
}
