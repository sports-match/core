package com.srr.rest;

import com.srr.dto.PlayerAnswerDto;
import com.srr.dto.PlayerSelfAssessmentRequest;
import com.srr.service.PlayerAnswerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.utils.ExecutionResult;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @PreAuthorize("@el.check('player-answer:list')")
    public ResponseEntity<Object> getAll(PlayerAnswerDto criteria, Pageable pageable) {
        return new ResponseEntity<>(playerAnswerService.queryAll(criteria, pageable), HttpStatus.OK);
    }
    
    @ApiOperation("Get all answers for a player")
    @GetMapping("/player/{playerId}")
    @PreAuthorize("@el.check('player-answer:list')")
    public ResponseEntity<List<PlayerAnswerDto>> getByPlayerId(@PathVariable Long playerId) {
        return new ResponseEntity<>(playerAnswerService.getByPlayerId(playerId), HttpStatus.OK);
    }
    
    @ApiOperation("Get answers by player ID and question category")
    @GetMapping("/player/{playerId}/category/{category}")
    @PreAuthorize("@el.check('player-answer:list')")
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
            @Validated @RequestBody PlayerSelfAssessmentRequest request) {
        String sport = (request.getSport() == null || request.getSport().isEmpty()) ? "Badminton" : request.getSport();
        String format = (request.getFormat() == null || request.getFormat().isEmpty()) ? "DOUBLES" : request.getFormat();
        return new ResponseEntity<>(
                playerAnswerService.submitSelfAssessment(request.getAnswers(), sport, format),
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
    @PreAuthorize("@el.check('player-answer:list')")
    public ResponseEntity<PlayerAnswerDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(playerAnswerService.findById(id), HttpStatus.OK);
    }
    
    @ApiOperation("Create a new player answer")
    @PostMapping
    @PreAuthorize("@el.check('player-answer:add')")
    public ResponseEntity<ExecutionResult> create(@Validated @RequestBody PlayerAnswerDto resources) {
        return new ResponseEntity<>(playerAnswerService.create(resources), HttpStatus.CREATED);
    }
    
    @ApiOperation("Update a player answer")
    @PutMapping
    @PreAuthorize("@el.check('player-answer:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody PlayerAnswerDto resources) {
        ExecutionResult result = playerAnswerService.update(resources);
        return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
    }
    
    @ApiOperation("Delete a player answer")
    @DeleteMapping("/{id}")
    @PreAuthorize("@el.check('player-answer:del')")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        ExecutionResult result = playerAnswerService.delete(id);
        return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
    }
}
