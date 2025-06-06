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

import com.srr.domain.Team;
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
public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {
    /**
     * Find all teams for a specific event
     * @param eventId ID of the event
     * @return List of teams
     */
    @Query("SELECT t FROM Team t WHERE t.event.id = :eventId")
    List<Team> findByEventId(@Param("eventId") Long eventId);
    
    /**
     * Find all teams for a specific event (alias for findByEventId)
     * @param eventId ID of the event
     * @return List of teams
     */
    List<Team> findAllByEventId(Long eventId);
}
