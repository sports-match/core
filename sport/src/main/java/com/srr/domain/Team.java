package com.srr.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "team")
public class Team implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id", hidden = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_group_id")
    private MatchGroup matchGroup;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "name")
    @ApiModelProperty(value = "Name")
    private String name;

    @Column(name = "team_size")
    @ApiModelProperty(value = "Team size")
    private int teamSize;
    
    @Column(name = "average_score")
    @ApiModelProperty(value = "Average team score based on player scores")
    private Double averageScore = 0.0;

    @OneToMany(mappedBy = "team")
    @ApiModelProperty(value = "teamPlayers")
    private List<TeamPlayer> teamPlayers;
}
