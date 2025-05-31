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
package com.srr.service.impl;

import com.srr.domain.Team;
import com.srr.dto.TeamDto;
import com.srr.dto.mapstruct.TeamMapper;
import com.srr.repository.TeamRepository;
import com.srr.service.TeamService;
import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Chanheng
 * @website https://eladmin.vip
 * @date 2025-05-30
 **/
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Override
    @Transactional(readOnly = true)
    public TeamDto findById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Team.class, "id", id.toString()));
        return teamMapper.toDto(team);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDto> findByEventId(Long eventId) {
        List<Team> teams = teamRepository.findByEventId(eventId);
        return teams.stream()
                .map(teamMapper::toDto)
                .collect(Collectors.toList());
    }
}
