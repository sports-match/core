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

import com.srr.dto.TeamPlayerDto;
import com.srr.dto.TeamPlayerReassignDto;
import com.srr.service.TeamPlayerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chanheng
 * @website https://eladmin.vip
 * @date 2025-05-26
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "Team Player Management")
@RequestMapping("/api/team-players")
public class TeamPlayerController {

    private final TeamPlayerService teamPlayerService;

    @GetMapping("/{id}")
    @ApiOperation("Get team player details")
    @PreAuthorize("hasAuthority('Organizer')")
    public ResponseEntity<TeamPlayerDto> getTeamPlayer(@PathVariable Long id) {
        return new ResponseEntity<>(teamPlayerService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/check-in")
    @Log("Check in player")
    @ApiOperation("Check in player for an event")
    @PreAuthorize("hasAuthority('Organizer')")
    public ResponseEntity<TeamPlayerDto> checkIn(@PathVariable Long id) {
        return new ResponseEntity<>(teamPlayerService.checkIn(id), HttpStatus.OK);
    }
    
    @PostMapping("/reassign")
    @Log("Reassign player to another team")
    @ApiOperation("Reassign player to another team")
    @PreAuthorize("hasAuthority('Organizer')")
    public ResponseEntity<TeamPlayerDto> reassignPlayer(@Validated @RequestBody TeamPlayerReassignDto dto) {
        return new ResponseEntity<>(teamPlayerService.reassignPlayer(dto), HttpStatus.OK);
    }
}
