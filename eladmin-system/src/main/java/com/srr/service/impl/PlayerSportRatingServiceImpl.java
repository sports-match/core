package com.srr.service.impl;

import com.srr.domain.PlayerSportRating;
import com.srr.dto.PlayerSportRatingDto;
import com.srr.repository.PlayerSportRatingRepository;
import com.srr.service.PlayerSportRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerSportRatingServiceImpl implements PlayerSportRatingService {
    private final PlayerSportRatingRepository playerSportRatingRepository;

    @Override
    public List<PlayerSportRatingDto> getRatingsForPlayer(Long playerId) {
        return playerSportRatingRepository.findByPlayerId(playerId)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public PlayerSportRatingDto getRatingForPlayerSportFormat(Long playerId, String sport, String format) {
        return playerSportRatingRepository.findByPlayerIdAndSportAndFormat(playerId, sport, format)
            .map(this::toDto).orElse(null);
    }

    private PlayerSportRatingDto toDto(PlayerSportRating entity) {
        PlayerSportRatingDto dto = new PlayerSportRatingDto();
        dto.setId(entity.getId());
        dto.setPlayerId(entity.getPlayerId());
        dto.setSport(entity.getSport());
        dto.setFormat(entity.getFormat());
        dto.setRateScore(entity.getRateScore());
        dto.setRateBand(entity.getRateBand());
        dto.setProvisional(entity.getProvisional());
        return dto;
    }
}
