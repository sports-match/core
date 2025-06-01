package com.srr.rest;

import com.srr.dto.PlayerAnswerDto;
import com.srr.service.PlayerAnswerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description PlayerAnswer controller for API endpoints
* @author Chanheng
* @date 2025-05-31
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "Player Self-Assessment Management")
@RequestMapping("/api/player-answers")
public class PlayerAnswerController {

    private final PlayerAnswerService playerAnswerService;
    
    @ApiOperation("Get all player answers with pagination")
    @GetMapping
    @PreAuthorize("@el.check('playerAnswer:list')")
    public ResponseEntity<Object> getAll(PlayerAnswerDto criteria, Pageable pageable) {
        return new ResponseEntity<>(playerAnswerService.queryAll(criteria, pageable), HttpStatus.OK);
    }
    
    @ApiOperation("Get all answers for a player")
    @GetMapping("/player/{playerId}")
    @PreAuthorize("@el.check('playerAnswer:list')")
    public ResponseEntity<List<PlayerAnswerDto>> getByPlayerId(@PathVariable Long playerId) {
        return new ResponseEntity<>(playerAnswerService.getByPlayerId(playerId), HttpStatus.OK);
    }
    
    @ApiOperation("Get answers by player ID and question category")
    @GetMapping("/player/{playerId}/category/{category}")
    @PreAuthorize("@el.check('playerAnswer:list')")
    public ResponseEntity<List<PlayerAnswerDto>> getByPlayerIdAndCategory(
            @PathVariable Long playerId, 
            @PathVariable String category) {
        return new ResponseEntity<>(
                playerAnswerService.getByPlayerIdAndCategory(playerId, category), 
                HttpStatus.OK);
    }
    
    @ApiOperation("Submit player self-assessment answers")
    @PostMapping("/submit-assessment")
    @PreAuthorize("@el.check('player-answer:create')")
    public ResponseEntity<List<PlayerAnswerDto>> submitSelfAssessment(
            @Validated @RequestBody List<PlayerAnswerDto> answers) {
        return new ResponseEntity<>(
                playerAnswerService.submitSelfAssessment(answers), 
                HttpStatus.CREATED);
    }
    
    @ApiOperation("Check if player has completed self-assessment")
    @GetMapping("/player/{playerId}/completed")
    @PreAuthorize("@el.check('player-answer:list')")
    public ResponseEntity<Boolean> hasCompletedSelfAssessment(@PathVariable Long playerId) {
        return new ResponseEntity<>(
                playerAnswerService.hasCompletedSelfAssessment(playerId), 
                HttpStatus.OK);
    }
    
    @ApiOperation("Get player answer by ID")
    @GetMapping("/{id}")
    @PreAuthorize("@el.check('playerAnswer:list')")
    public ResponseEntity<PlayerAnswerDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(playerAnswerService.findById(id), HttpStatus.OK);
    }
    
    @ApiOperation("Create a new player answer")
    @PostMapping
    @PreAuthorize("@el.check('playerAnswer:add')")
    public ResponseEntity<PlayerAnswerDto> create(@Validated @RequestBody PlayerAnswerDto resources) {
        return new ResponseEntity<>(playerAnswerService.create(resources), HttpStatus.CREATED);
    }
    
    @ApiOperation("Update a player answer")
    @PutMapping
    @PreAuthorize("@el.check('playerAnswer:edit')")
    public ResponseEntity<Void> update(@Validated @RequestBody PlayerAnswerDto resources) {
        playerAnswerService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @ApiOperation("Delete a player answer")
    @DeleteMapping("/{id}")
    @PreAuthorize("@el.check('playerAnswer:del')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playerAnswerService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
