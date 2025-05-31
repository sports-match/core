package com.srr.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "team_player")
public class TeamPlayer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id", hidden = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "is_checked_in")
    private boolean isCheckedIn;

    @PrePersist
    @PreUpdate
    public void updateTeamAverageScore() {
        if (team != null) {
            calculateAndUpdateTeamAverageScore(team);
        }
    }

    /**
     * Calculates and updates the average score for the team based on players' rate scores
     *
     * @param team The team to update the score for
     */
    private void calculateAndUpdateTeamAverageScore(Team team) {
        List<TeamPlayer> teamPlayers = team.getTeamPlayers();
        // Handle case when team has no players yet (current entity might not be in the list)
        if (teamPlayers == null || (teamPlayers.isEmpty() && player == null)) {
            team.setAverageScore(0.0);
            return;
        }

        double totalScore = 0;
        int playerCount = 0;

        // Include current player if it has a score and isn't in the team list yet
        if (player != null && player.getRateScore() != null) {
            // Check if this is a new team player not yet in the list
            boolean isNewPlayer = teamPlayers
                    .stream()
                    .noneMatch(tp -> tp.getId() != null && tp.getId().equals(this.getId()));

            if (isNewPlayer) {
                totalScore += player.getRateScore();
                playerCount++;
            }
        }

        // Add scores from existing team players
        for (TeamPlayer teamPlayer : teamPlayers) {
            if (teamPlayer.getPlayer() != null &&
                    teamPlayer.getPlayer().getRateScore() != null) {
                totalScore += teamPlayer.getPlayer().getRateScore();
                playerCount++;
            }
        }

        double averageScore = playerCount > 0 ? totalScore / playerCount : 0;
        team.setAverageScore(averageScore);
    }
}
