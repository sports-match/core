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
package com.srr.service;

import com.srr.dto.TeamDto;
import java.util.List;

/**
 * @author Chanheng
 * @website https://eladmin.vip
 * @description Service interface for Team
 * @date 2025-05-30
 **/
public interface TeamService {

    /**
     * Find a team by ID
     * @param id Team ID
     * @return TeamDto
     */
    TeamDto findById(Long id);

    /**
     * Find all teams for a specific event
     * @param eventId Event ID
     * @return List of TeamDto
     */
    List<TeamDto> findByEventId(Long eventId);
}
