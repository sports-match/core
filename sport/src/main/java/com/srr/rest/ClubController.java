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

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import com.srr.domain.Club;
import com.srr.service.ClubService;
import com.srr.dto.ClubQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.ExecutionResult;
import com.srr.dto.ClubDto;
import java.util.HashMap;
import java.util.Map;

/**
* @website https://eladmin.vip
* @author Chanheng
* @date 2025-05-18
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "Club Management")
@Slf4j
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    @ApiOperation("Query clubs")
    @PreAuthorize("@el.check('club:list')")
    public ResponseEntity<PageResult<ClubDto>> queryClub(ClubQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(clubService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiOperation("Get club by ID")
    @PreAuthorize("@el.check('club:list')")
    public ResponseEntity<ClubDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(clubService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    @Log("Add clubs")
    @ApiOperation("Add clubs")
    @PreAuthorize("@el.check('club:add')")
    public ResponseEntity<Object> createClub(@Validated @RequestBody Club resources){
        ExecutionResult result = clubService.create(resources);
        return new ResponseEntity<>(result.toMap(), HttpStatus.CREATED);
    }

    @PutMapping
    @Log("Modify clubs")
    @ApiOperation("Modify clubs")
    @PreAuthorize("@el.check('club:edit')")
    public ResponseEntity<Object> updateClub(@Validated @RequestBody Club resources){
        ExecutionResult result = clubService.update(resources);
        return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
    }

    @DeleteMapping
    @Log("Delete clubs")
    @ApiOperation("Delete clubs")
    @PreAuthorize("@el.check('club:del')")
    public ResponseEntity<Object> deleteClub(@RequestBody Long[] ids) {
        ExecutionResult result = clubService.deleteAll(ids);
        return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
    }
}