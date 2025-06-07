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

import com.srr.domain.*;
import com.srr.enumeration.EventStatus;
import com.srr.enumeration.Format;
import com.srr.repository.*;
import me.zhengjie.exception.EntityNotFoundException;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import com.srr.service.EventService;
import com.srr.dto.EventDto;
import com.srr.dto.EventQueryCriteria;
import com.srr.dto.mapstruct.EventMapper;
import com.srr.dto.JoinEventDto;
import me.zhengjie.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import me.zhengjie.utils.ExecutionResult;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import me.zhengjie.utils.PageResult;

/**
 * @author Chanheng
 * @website https://eladmin.vip
 * @description 服务实现
 * @date 2025-05-18
 **/
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final TeamRepository teamRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final MatchGroupRepository matchGroupRepository;
    private final MatchRepository matchRepository;
    private final WaitListRepository waitListRepository;

    @Override
    public PageResult<EventDto> queryAll(EventQueryCriteria criteria, Pageable pageable) {
        Page<Event> page = eventRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(eventMapper::toDto));
    }

    @Override
    public List<EventDto> queryAll(EventQueryCriteria criteria) {
        return eventMapper.toDto(eventRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public EventDto findById(Long id) {
        Event event = eventRepository.findById(id).orElseGet(Event::new);
        ValidationUtil.isNull(event.getId(), "Event", "id", id);
        return eventMapper.toDto(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventDto create(Event resources) {
        resources.setStatus(EventStatus.DRAFT);
        final var result = eventRepository.save(resources);
        return eventMapper.toDto(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventDto update(Event resources) {
        Event event = eventRepository.findById(resources.getId()).orElseGet(Event::new);
        ValidationUtil.isNull(event.getId(), "Event", "id", resources.getId());
        event.copy(resources);
        final var result = eventRepository.save(event);
        return eventMapper.toDto(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventDto updateStatus(Long id, EventStatus status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, "id", String.valueOf(id)));

        // Only update the status field
        event.setStatus(status);
        if (status == EventStatus.CHECK_IN) {
            event.setCheckInAt(Timestamp.from(Instant.now()));
        }

        final var result = eventRepository.save(event);
        return eventMapper.toDto(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventDto joinEvent(JoinEventDto joinEventDto) {
        // Find the event
        Event event = eventRepository.findById(joinEventDto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException(Event.class, "id", String.valueOf(joinEventDto.getEventId())));
        
        // Check if event allows joining
        if (event.getStatus() != EventStatus.OPEN) {
            throw new BadRequestException("Event is not open for joining");
        }
        
        // Check if event is full and handle waitlist
        boolean isWaitList = joinEventDto.getJoinWaitList() != null && joinEventDto.getJoinWaitList();
        if (event.getMaxParticipants() != null && 
            event.getCurrentParticipants() >= event.getMaxParticipants() && 
            !isWaitList) {
            if (!event.isAllowWaitList()) {
                throw new BadRequestException("Event is full and does not allow waitlist");
            }
            // Set joinWaitList to true if event is full and waitlist is allowed
            isWaitList = true;
        }
        
        // Handle team-related logic
        if (joinEventDto.getTeamId() != null) {
            // Add player to existing team
            Team team = teamRepository.findById(joinEventDto.getTeamId())
                    .orElseThrow(() -> new EntityNotFoundException(Team.class, "id", String.valueOf(joinEventDto.getTeamId())));
            
            // Verify team belongs to this event
            if (!team.getEvent().getId().equals(event.getId())) {
                throw new BadRequestException("Team does not belong to this event");
            }
            
            // Check if player is already in the team
            if (teamPlayerRepository.existsByTeamIdAndPlayerId(team.getId(), joinEventDto.getPlayerId())) {
                throw new BadRequestException("Player is already in this team");
            }
            
            // Check if team is full
            if (team.getTeamPlayers().size() >= team.getTeamSize()) {
                throw new BadRequestException("Team is already full");
            }
            
            // Add player to team
            TeamPlayer teamPlayer = new TeamPlayer();
            teamPlayer.setTeam(team);
            Player player = new Player();
            player.setId(joinEventDto.getPlayerId());
            teamPlayer.setPlayer(player);
            teamPlayer.setCheckedIn(false);
            teamPlayerRepository.save(teamPlayer);
            
            // Save team to ensure averageScore is updated
            teamRepository.save(team);
        } else {
            // No teamId provided, so create a new team regardless of format
            Team team = new Team();
            team.setEvent(event);
            
            if (event.getFormat() == Format.SINGLE) {
                team.setName("Player " + joinEventDto.getPlayerId());
                team.setTeamSize(1);
            } else if (event.getFormat() == Format.DOUBLE) {
                team.setName("New Team");
                team.setTeamSize(2);
            } else {
                team.setName("New Team");
                team.setTeamSize(4); // Default size for TEAM format
            }
            
            Team savedTeam = teamRepository.save(team);
            
            // Add player as the first member of the new team
            TeamPlayer teamPlayer = new TeamPlayer();
            teamPlayer.setTeam(savedTeam);
            Player player = new Player();
            player.setId(joinEventDto.getPlayerId());
            teamPlayer.setPlayer(player);
            teamPlayer.setCheckedIn(false);
            teamPlayerRepository.save(teamPlayer);
            
            // Save team again to ensure averageScore is updated
            teamRepository.save(savedTeam);
        }
        
        // Update participant count if not joining waitlist
        if (!isWaitList) {
            event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        }
        
        // Save and return updated event
        final var result = eventRepository.save(event);
        return eventMapper.toDto(result);
    }

    @Override
    @Transactional
    public ExecutionResult deleteAll(Long[] ids) {
        for (Long id : ids) {
            // Get the event to check if it exists
            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(Event.class, "id", id.toString()));
                    
            // Delete match groups and matches for this event
            List<MatchGroup> matchGroups = matchGroupRepository.findAllByEventId(id);
            for (MatchGroup matchGroup : matchGroups) {
                // Delete all matches in this group
                List<Match> matches = matchRepository.findAllByMatchGroupId(matchGroup.getId());
                matchRepository.deleteAll(matches);
            }
            matchGroupRepository.deleteAll(matchGroups);
            
            // Delete teams and team players for this event
            List<Team> teams = teamRepository.findAllByEventId(id);
            for (Team team : teams) {
                // Delete all team players in this team
                List<TeamPlayer> teamPlayers = teamPlayerRepository.findAllByTeamId(team.getId());
                teamPlayerRepository.deleteAll(teamPlayers);
            }
            teamRepository.deleteAll(teams);
            
            // Delete wait list entries for this event
            List<WaitList> waitListEntries = waitListRepository.findAllByEventId(id);
            waitListRepository.deleteAll(waitListEntries);
            
            // Delete the event
            eventRepository.deleteById(id);
        }
        
        return ExecutionResult.of(null, Map.of("count", ids.length, "ids", ids));
    }

    @Override
    public void download(List<EventDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (EventDto event : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("名称", event.getName());
            map.put("描述", event.getDescription());
            map.put("SINGLE, DOUBLE", event.getFormat());
            map.put("位置", event.getLocation());
            map.put("图片", event.getImage());
            map.put("创建时间", event.getCreateTime());
            map.put("更新时间", event.getUpdateTime());
            map.put("排序", event.getSort());
            map.put("是否启用", event.getEnabled());
            map.put("时间", event.getEventTime());
            map.put(" clubId", event.getClubId());
            map.put(" createBy", event.getCreateBy());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}