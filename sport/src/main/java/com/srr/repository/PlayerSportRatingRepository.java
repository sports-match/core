package com.srr.repository;

import com.srr.domain.PlayerSportRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerSportRatingRepository extends JpaRepository<PlayerSportRating, Long> {
    Optional<PlayerSportRating> findByPlayerIdAndSportAndFormat(Long playerId, String sport, String format);
    List<PlayerSportRating> findByPlayerId(Long playerId);
}
