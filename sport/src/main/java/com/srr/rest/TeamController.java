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
package com.srr.rest;

import com.srr.dto.TeamDto;
import com.srr.service.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chanheng
 * @website https://eladmin.vip
 * @date 2025-05-30
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "Team Management")
@RequestMapping("/api")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/team/{id}")
    @ApiOperation("Get team details")
    @PreAuthorize("@el.check('event:list')")
    public ResponseEntity<TeamDto> getTeam(@PathVariable Long id) {
        return new ResponseEntity<>(teamService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/event/{eventId}/team")
    @ApiOperation("Get all teams for an event")
    @PreAuthorize("@el.check('event:list')")
    public ResponseEntity<List<TeamDto>> getTeamsByEvent(@PathVariable Long eventId) {
        return new ResponseEntity<>(teamService.findByEventId(eventId), HttpStatus.OK);
    }
}
