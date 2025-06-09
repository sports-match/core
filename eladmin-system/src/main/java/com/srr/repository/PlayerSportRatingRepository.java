package com.srr.repository;

import com.srr.domain.PlayerSportRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PlayerSportRatingRepository extends JpaRepository<PlayerSportRating, Long>, JpaSpecificationExecutor<PlayerSportRating> {
    Optional<PlayerSportRating> findByPlayerIdAndSportAndFormat(Long playerId, String sport, String format);
    List<PlayerSportRating> findByPlayerId(Long playerId);
}
