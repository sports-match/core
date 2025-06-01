package com.srr.repository;

import com.srr.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
* @description Question repository for database operations
* @author Chanheng
* @date 2025-05-31
**/
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    
    /**
     * Find all questions ordered by category and order index
     * @return List of questions
     */
    @Query("SELECT q FROM Question q ORDER BY q.category, q.orderIndex")
    List<Question> findAllOrdered();
    
    /**
     * Find questions by category
     * @param category The category name
     * @return List of questions in the category
     */
    List<Question> findByCategoryOrderByOrderIndex(String category);
}
