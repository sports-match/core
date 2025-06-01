package com.srr.service.impl;

import com.srr.domain.Question;
import com.srr.dto.QuestionDto;
import com.srr.repository.QuestionRepository;
import com.srr.service.QuestionService;
import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.QueryHelp;
import me.zhengjie.utils.ValidationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @description Question service implementation
* @author Chanheng
* @date 2025-05-31
**/
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public PageResult<QuestionDto> queryAll(QuestionDto criteria, Pageable pageable) {
        Page<Question> page = questionRepository.findAll((root, query, cb) -> 
                QueryHelp.getPredicate(root, criteria, cb), pageable);
        return PageUtil.toPage(page.map(this::toDto));
    }

    @Override
    public List<QuestionDto> getAllForSelfAssessment() {
        return questionRepository.findAllOrdered().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionDto> getByCategory(String category) {
        return questionRepository.findByCategoryOrderByOrderIndex(category).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionDto create(QuestionDto resources) {
        Question question = new Question();
        question.setText(resources.getText());
        question.setCategory(resources.getCategory());
        question.setOrderIndex(resources.getOrderIndex());
        question.setMinValue(resources.getMinValue());
        question.setMaxValue(resources.getMaxValue());
        return toDto(questionRepository.save(question));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QuestionDto resources) {
        Question question = questionRepository.findById(resources.getId())
                .orElseThrow(() -> new BadRequestException("Question not found"));
        question.setText(resources.getText());
        question.setCategory(resources.getCategory());
        question.setOrderIndex(resources.getOrderIndex());
        question.setMinValue(resources.getMinValue());
        question.setMaxValue(resources.getMaxValue());
        questionRepository.save(question);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public QuestionDto findById(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        ValidationUtil.isNull(question, "Question", "id", id);
        return toDto(question.get());
    }

    private QuestionDto toDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setCategory(question.getCategory());
        dto.setOrderIndex(question.getOrderIndex());
        dto.setMinValue(question.getMinValue());
        dto.setMaxValue(question.getMaxValue());
        dto.setCreateTime(question.getCreateTime());
        dto.setUpdateTime(question.getUpdateTime());
        return dto;
    }
}
