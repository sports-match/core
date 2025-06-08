package com.srr.service.impl;

import com.srr.domain.EventOrganizer;
import com.srr.repository.EventOrganizerRepository;
import com.srr.service.EventOrganizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
