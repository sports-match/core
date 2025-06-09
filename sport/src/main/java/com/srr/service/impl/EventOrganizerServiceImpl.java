package com.srr.service.impl;

import com.srr.domain.EventOrganizer;
import com.srr.enumeration.VerificationStatus;
import com.srr.repository.EventOrganizerRepository;
import com.srr.service.EventOrganizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.EntityNotFoundException;
import me.zhengjie.utils.ExecutionResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of EventOrganizerService for managing event organizers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventOrganizerServiceImpl implements EventOrganizerService {

    private final EventOrganizerRepository eventOrganizerRepository;

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
    public ExecutionResult updateVerificationStatus(Long organizerId, VerificationStatus status) {
        log.info("Updating verification status for organizer ID: {} to {}", organizerId, status);
        EventOrganizer organizer = eventOrganizerRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException(EventOrganizer.class, "id", organizerId));

        organizer.setVerificationStatus(status);
        EventOrganizer updatedOrganizer = eventOrganizerRepository.save(organizer);
        log.info("Successfully updated verification status for organizer ID: {}", updatedOrganizer.getId());
        return ExecutionResult.of(updatedOrganizer.getId());
    }
}
