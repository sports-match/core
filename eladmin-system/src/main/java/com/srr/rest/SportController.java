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

import com.srr.domain.Sport;
import com.srr.dto.SportDto;
import com.srr.dto.SportQueryCriteria;
import com.srr.service.SportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.utils.ExecutionResult;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @website https://eladmin.vip
* @author Chanheng
* @date 2025-05-17
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "Sport Management")
@RequestMapping("/api/sports")
public class SportController {

    private final SportService sportService;

    @ApiOperation("Health check endpoint")
    @AnonymousGetMapping(value = "/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping
    @ApiOperation("Query sport")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<PageResult<SportDto>> querySport(SportQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(sportService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiOperation("Get sport by ID")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<SportDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(sportService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    @Log("Add sport")
    @ApiOperation("Add sport")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Object> createSport(@Validated @RequestBody Sport resources){
        ExecutionResult result = sportService.create(resources);
        return new ResponseEntity<>(result.toMap(), HttpStatus.CREATED);
    }

    @PutMapping
    @Log("Modify sport")
    @ApiOperation("Modify sport")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Object> updateSport(@Validated @RequestBody Sport resources){
        ExecutionResult result = sportService.update(resources);
        return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
    }

    @DeleteMapping
    @Log("Delete sport")
    @ApiOperation("Delete sport")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Object> deleteSport(@RequestBody Long[] ids) {
        ExecutionResult result = sportService.deleteAll(ids);
        return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
    }
}