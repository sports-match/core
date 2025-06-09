package com.srr.rest;

import com.srr.domain.PlayerSportRating;
import com.srr.dto.PlayerSportRatingDto;
import com.srr.repository.PlayerSportRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/player-sport-rating")
@RequiredArgsConstructor
public class PlayerSportRatingController {
    private final PlayerSportRatingRepository playerSportRatingRepository;

    @GetMapping("/player/{playerId}")
    public List<PlayerSportRatingDto> getRatingsForPlayer(@PathVariable Long playerId) {
        return playerSportRatingRepository.findByPlayerId(playerId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/player/{playerId}/sport/{sport}/format/{format}")
    public PlayerSportRatingDto getRatingForPlayerSportFormat(@PathVariable Long playerId, @PathVariable String sport, @PathVariable String format) {
        return playerSportRatingRepository.findByPlayerIdAndSportAndFormat(playerId, sport, format)
                .map(this::toDto)
                .orElse(null);
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
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}
