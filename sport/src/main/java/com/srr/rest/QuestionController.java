package com.srr.rest;

import com.srr.dto.QuestionDto;
import com.srr.service.QuestionService;
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
* @description Question controller for API endpoints
* @author Chanheng
* @date 2025-05-31
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "Self-Assessment Questions Management")
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    
    @ApiOperation("Get all questions with pagination")
    @GetMapping
    @PreAuthorize("@el.check('question:list')")
    public ResponseEntity<Object> getAll(QuestionDto criteria, Pageable pageable) {
        return new ResponseEntity<>(questionService.queryAll(criteria, pageable), HttpStatus.OK);
    }
    
    @ApiOperation("Get all questions for self-assessment")
    @GetMapping("/self-assessment")
    @PreAuthorize("@el.check('question:list')")
    public ResponseEntity<List<QuestionDto>> getAllForSelfAssessment() {
        return new ResponseEntity<>(questionService.getAllForSelfAssessment(), HttpStatus.OK);
    }
    
    @ApiOperation("Get questions by category")
    @GetMapping("/category/{category}")
    @PreAuthorize("@el.check('question:list')")
    public ResponseEntity<List<QuestionDto>> getByCategory(@PathVariable String category) {
        return new ResponseEntity<>(questionService.getByCategory(category), HttpStatus.OK);
    }
    
    @ApiOperation("Get question by ID")
    @GetMapping("/{id}")
    @PreAuthorize("@el.check('question:list')")
    public ResponseEntity<QuestionDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(questionService.findById(id), HttpStatus.OK);
    }
    
    @ApiOperation("Create a new question")
    @PostMapping
    @PreAuthorize("@el.check('question:add')")
    public ResponseEntity<QuestionDto> create(@Validated @RequestBody QuestionDto resources) {
        return new ResponseEntity<>(questionService.create(resources), HttpStatus.CREATED);
    }
    
    @ApiOperation("Update a question")
    @PutMapping
    @PreAuthorize("@el.check('question:edit')")
    public ResponseEntity<Void> update(@Validated @RequestBody QuestionDto resources) {
        questionService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @ApiOperation("Delete a question")
    @DeleteMapping("/{id}")
    @PreAuthorize("@el.check('question:del')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
