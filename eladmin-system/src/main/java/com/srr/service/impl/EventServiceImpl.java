package com.srr.service.impl;

import com.srr.domain.*;
import com.srr.dto.EventDto;
import com.srr.dto.EventQueryCriteria;
import com.srr.dto.JoinEventDto;
import com.srr.dto.enums.EventTimeFilter;
import com.srr.dto.mapstruct.EventMapper;
import com.srr.enumeration.EventStatus;
import com.srr.enumeration.Format;
import com.srr.enumeration.VerificationStatus;
import com.srr.repository.*;
import com.srr.service.EventService;
import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityNotFoundException;
import me.zhengjie.utils.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

/**
 * @author Chanheng
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
    private final PlayerSportRatingRepository playerSportRatingRepository;
    private final EventOrganizerRepository eventOrganizerRepository;

    @Override
    public PageResult<EventDto> queryAll(EventQueryCriteria criteria, Pageable pageable) {
        Page<Event> page = eventRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = QueryHelp.getPredicate(root, criteria, criteriaBuilder);
            if (criteria.getEventTimeFilter() != null) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                if (criteria.getEventTimeFilter() == EventTimeFilter.UPCOMING) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("eventTime"), now));
                } else if (criteria.getEventTimeFilter() == EventTimeFilter.PAST) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThan(root.get("eventTime"), now));
                }
            }
            return predicate;
        }, pageable);
        return PageUtil.toPage(page.map(eventMapper::toDto));
    }

    @Override
    public List<EventDto> queryAll(EventQueryCriteria criteria) {
        return eventMapper.toDto(eventRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = QueryHelp.getPredicate(root, criteria, criteriaBuilder);
            if (criteria.getEventTimeFilter() != null) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                if (criteria.getEventTimeFilter() == EventTimeFilter.UPCOMING) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("eventTime"), now));
                } else if (criteria.getEventTimeFilter() == EventTimeFilter.PAST) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThan(root.get("eventTime"), now));
                }
            }
            return predicate;
        }));
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
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // Check if the current user is an organizer and if they are verified
        List<EventOrganizer> organizerList = eventOrganizerRepository.findByUserId(currentUserId);
        if (!organizerList.isEmpty()) {
            EventOrganizer organizer = organizerList.get(0); // Assuming one user has one organizer profile
            if (organizer.getVerificationStatus() != VerificationStatus.VERIFIED) {
                throw new BadRequestException("Organizer account is not verified. Event creation is not allowed.");
            }
        }
        // If organizerList is empty, it means the user is not an organizer (e.g., an admin),
        // so the check is bypassed. Permission to create is handled by @PreAuthorize.

        resources.setStatus(EventStatus.DRAFT);
        // Set the creator of the event using the Long ID directly
        if (resources.getCreateBy() == null) { // Event.java has 'createBy' as Long
             resources.setCreateBy(currentUserId);
        }

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
        // Self-assessment enforcement: require PlayerSportRating for badminton/doubles
        Long playerId = joinEventDto.getPlayerId();
        // For phase 1, sport is always "Badminton", format is always "DOUBLES"
        if (playerId == null) {
            throw new BadRequestException("Player ID is required to join event");
        }
        var ratingOpt = playerSportRatingRepository.findByPlayerIdAndSportAndFormat(playerId, "Badminton", "DOUBLES");
        if (ratingOpt.isEmpty() || ratingOpt.get().getRateScore() == null || ratingOpt.get().getRateScore() <= 0) {
            throw new BadRequestException("Please complete your self-assessment before joining an event.");
        }

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
            (event.getCurrentParticipants() != null && event.getCurrentParticipants() >= event.getMaxParticipants()) &&
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

            // Update current participants if not joining waitlist
            if (!isWaitList) {
                event.setCurrentParticipants((event.getCurrentParticipants() == null ? 0 : event.getCurrentParticipants()) + 1);
            } else {
                // Add to waitlist
                WaitList waitListEntry = new WaitList();
                waitListEntry.setEventId(event.getId());
                Player waitingPlayer = new Player();
                waitingPlayer.setId(joinEventDto.getPlayerId());
                waitListEntry.setPlayerId(waitingPlayer.getId());
                waitListRepository.save(waitListEntry);
            }
        }
        
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExecutionResult deleteAll(Long[] ids) {
        List<Long> successfulDeletes = new ArrayList<>();
        List<Long> failedDeletes = new ArrayList<>();

        for (Long id : ids) {
            Optional<Event> eventOptional = eventRepository.findById(id);
            if (eventOptional.isPresent()) {
                Event event = eventOptional.get();
                // Check if the event status allows deletion (e.g., DRAFT or CLOSED)
                if (event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.CLOSED) {
                    // Cascade delete related entities
                    // Delete matches associated with match groups of this event
                    List<MatchGroup> matchGroups = matchGroupRepository.findAllByEventId(id);
                    for (MatchGroup group : matchGroups) {
                        matchRepository.deleteByMatchGroupId(group.getId());
                    }
                    // Delete match groups
                    matchGroupRepository.deleteByEventId(id);
                    // Delete team players
                    teamPlayerRepository.deleteByTeamEventId(id);
                    // Delete teams
                    teamRepository.deleteByEventId(id);
                    // Delete waitlist entries
                    waitListRepository.deleteByEventId(id);
                    // Finally, delete the event itself
                    eventRepository.deleteById(id);
                    successfulDeletes.add(id);
                } else {
                    // Event is in a state that does not allow deletion
                    failedDeletes.add(id);
                }
            } else {
                // Event not found
                failedDeletes.add(id);
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("successfulDeletes", successfulDeletes.size());
        data.put("failedDeletes", failedDeletes.size());
        data.put("details", Map.of("successfulIds", successfulDeletes, "failedIds", failedDeletes));

        // Use a common ID for the operation, or null if not applicable for bulk
        Long operationId = (ids != null && ids.length > 0) ? ids[0] : null;
        if (!failedDeletes.isEmpty()) {
             // If there are failures, the 'id' in ExecutionResult might represent the first failed ID for context
            operationId = failedDeletes.get(0);
        }
        return ExecutionResult.of(operationId, data);
    }

    @Override
    public void download(List<EventDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (EventDto event : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("Event Name", event.getName());
            map.put("Description", event.getDescription());
            map.put("Event Time", event.getEventTime());
            map.put("Location", event.getLocation());
            map.put("Format", event.getFormat());
            map.put("Max Participants", event.getMaxParticipants());
            map.put("Current Participants", event.getCurrentParticipants());
            map.put("Status", event.getStatus());
            map.put("Allow WaitList", event.isAllowWaitList());
            map.put("Check-in At", event.getCheckInAt());
            map.put("Created By ID", event.getCreateBy());
            map.put("Create Time", event.getCreateTime());
            map.put("Update Time", event.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void validateOrganizerClubPermission(Long organizerId, Long clubId) {
        EventOrganizer organizer = eventOrganizerRepository.findById(organizerId)
            .orElseThrow(() -> new IllegalArgumentException("Organizer not found"));
        boolean allowed = organizer.getClubs().stream().anyMatch(club -> club.getId().equals(clubId));
        if (!allowed) {
            throw new org.springframework.security.access.AccessDeniedException("Organizer is not allowed to manage this club");
        }
    }
}