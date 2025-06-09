package com.srr.service.impl;

import com.srr.domain.Club;
import com.srr.domain.EventOrganizer;
import com.srr.repository.ClubRepository;
import com.srr.repository.EventOrganizerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.utils.ExecutionResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of EventOrganizerService for managing event organizers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventOrganizerServiceImpl implements com.srr.service.EventOrganizerService {

    private final EventOrganizerRepository eventOrganizerRepository;
    private final ClubRepository clubRepository;

    @Override
    @Transactional
    public ExecutionResult create(EventOrganizer resources) {
        EventOrganizer savedOrganizer = eventOrganizerRepository.save(resources);
        return ExecutionResult.of(savedOrganizer.getId());
    }

    @Override
    public List<EventOrganizer> findByUserId(Long userId) {
        return eventOrganizerRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void linkClubs(Long organizerId, List<Long> clubIds) {
        EventOrganizer organizer = eventOrganizerRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found"));
        Set<Club> clubs = new HashSet<>(clubRepository.findAllById(clubIds));
        organizer.setClubs(clubs);
        eventOrganizerRepository.save(organizer);
    }

    @Override
    public Set<Club> getClubs(Long organizerId) {
        EventOrganizer organizer = eventOrganizerRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found"));
        return organizer.getClubs();
    }
}
