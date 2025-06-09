package com.srr.service;

import com.srr.dto.PlayerSportRatingDto;
import java.util.List;

public interface PlayerSportRatingService {
    List<PlayerSportRatingDto> getRatingsForPlayer(Long playerId);
    PlayerSportRatingDto getRatingForPlayerSportFormat(Long playerId, String sport, String format);
}
