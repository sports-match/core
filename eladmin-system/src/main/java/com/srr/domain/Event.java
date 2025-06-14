/*
*  Copyright 2019-2025 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package com.srr.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.srr.enumeration.EventStatus;
import com.srr.enumeration.Format;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* @website https://eladmin.vip
* @description /
* @author Chanheng
* @date 2025-05-18
**/
@Entity
@Getter
@Setter
@Table(name="event")
@SQLDelete(sql = "update event set status = 'DELETED' where id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "status != 'DELETED'")
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id", hidden = true)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "Name")
    private String name;

    @Column(name = "description")
    @ApiModelProperty(value = "Description")
    private String description;

    @Column(name = "format", nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "SINGLE, DOUBLE")
    private Format format;

    @Column(name = "location")
    @ApiModelProperty(value = "Location")
    private String location;

    @Column(name = "image")
    @ApiModelProperty(value = "Image")
    private String image;

    @Column(name = "create_time")
    @CreationTimestamp
    @ApiModelProperty(value = "Creation time", hidden = true)
    private Timestamp createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    @ApiModelProperty(value = "Update time", hidden = true)
    private Timestamp updateTime;

    @Column(name = "check_in_at")
    @ApiModelProperty(value = "Check in time", hidden = true)
    private Timestamp checkInAt;

    @Column(name = "group_count")
    @ApiModelProperty(value = "Number of groups")
    private Integer groupCount;

    @Column(name = "sort")
    @ApiModelProperty(value = "Sort")
    private Integer sort;

    @Column(name = "enabled")
    @ApiModelProperty(value = "Enabled")
    private Boolean enabled;

    @Column(name = "event_time", nullable = false)
    @NotNull
    @ApiModelProperty(value = "Time")
    private Timestamp eventTime;

    @Column(name = "club_id", nullable = false)
    @NotNull
    @ApiModelProperty(value = "clubId")
    private Long clubId;

    @Column(name = "public_link")
    @ApiModelProperty(value = "publicLink", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String publicLink;

    @NotNull
    @Column(name = "sport_id", nullable = false)
    @ApiModelProperty(value = "sportId")
    private Long sportId;

    @Column(name = "create_by")
    @ApiModelProperty(value = "createBy")
    private Long createBy;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "status")
    private EventStatus status;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "allow_wait_list")
    private boolean allowWaitList;

    @Column(name = "current_participants")
    @ApiModelProperty(value = "Current number of participants")
    private Integer currentParticipants = 0;

    @Column(name = "max_participants")
    @ApiModelProperty(value = "Maximum number of participants")
    private Integer maxParticipants;

    @Column(name = "poster_image")
    private String posterImage;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "event_tag",
               joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    @ApiModelProperty(value = "Tags associated with the event")
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private EventOrganizer organizer;

    @ManyToMany
    @JoinTable(name = "event_co_host_player",
            joinColumns = {@JoinColumn(name = "event_id",referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "player_id",referencedColumnName = "id")})
    private List<Player> coHostPlayers = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<MatchGroup> matchGroups = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<Team> teams = new ArrayList<>();

    public void copy(Event source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
