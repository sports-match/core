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

import com.srr.domain.TeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
* @website https://eladmin.vip
* @author Chanheng
* @date 2025-05-25
**/
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, Long>, JpaSpecificationExecutor<TeamPlayer> {
    boolean existsByTeamIdAndPlayerId(Long teamId, Long playerId);
    
    TeamPlayer findByTeamIdAndPlayerId(Long teamId, Long playerId);
    
    @Query("SELECT tp FROM TeamPlayer tp JOIN tp.team t JOIN t.event e WHERE e.id = :eventId")
    List<TeamPlayer> findByEventId(@Param("eventId") Long eventId);
    
    /**
     * Find all team player entries for a specific player by user ID
     * @param userId User's ID
     * @return List of team player entries
     */
    @Query("SELECT tp FROM TeamPlayer tp JOIN tp.player p WHERE p.userId = :userId")
    List<TeamPlayer> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find team player entries by team IDs
     * @param teamIds List of team IDs
     * @return List of team player entries
     */
    @Query("SELECT tp FROM TeamPlayer tp WHERE tp.team.id IN :teamIds")
    List<TeamPlayer> findByTeamIdIn(@Param("teamIds") List<Long> teamIds);
}
