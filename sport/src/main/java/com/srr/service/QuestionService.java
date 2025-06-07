package com.srr.service;

import com.srr.dto.QuestionDto;
import me.zhengjie.utils.ExecutionResult;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
* @description Question service interface
* @author Chanheng
* @date 2025-05-31
**/
public interface QuestionService {

    /**
     * Get all questions
     * @param criteria Filter criteria
     * @param pageable Pagination information
     * @return PageResult containing a list of QuestionDto objects and total count
     */
    PageResult<QuestionDto> queryAll(QuestionDto criteria, Pageable pageable);
    
    /**
     * Get all questions for player self-assessment
     * @return List of questions ordered by category and order index
     */
    List<QuestionDto> getAllForSelfAssessment();
    
    /**
     * Get questions by category
     * @param category The category name
     * @return List of questions in the category
     */
    List<QuestionDto> getByCategory(String category);
    
    /**
     * Create a new question
     * @param resources QuestionDto object
     * @return Created QuestionDto
     */
    QuestionDto create(QuestionDto resources);
    
    /**
     * Update question
     * @param resources Question DTO
     * @return ExecutionResult containing the updated entity ID
     */
    ExecutionResult update(QuestionDto resources);

    /**
     * Delete question
     * @param id Question ID
     * @return ExecutionResult containing the deleted entity ID
     */
    ExecutionResult delete(Long id);
    
    /**
     * Get a question by ID
     * @param id Question ID
     * @return QuestionDto object
     */
    QuestionDto findById(Long id);
}
