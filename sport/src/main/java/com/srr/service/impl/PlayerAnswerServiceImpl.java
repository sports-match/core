package com.srr.service.impl;

import com.srr.domain.PlayerAnswer;
import com.srr.domain.Question;
import com.srr.dto.PlayerAnswerDto;
import com.srr.repository.PlayerAnswerRepository;
import com.srr.repository.PlayerRepository;
import com.srr.repository.QuestionRepository;
import com.srr.service.PlayerAnswerService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @description PlayerAnswer service implementation
* @author Chanheng
* @date 2025-05-31
**/
@Service
@RequiredArgsConstructor
public class PlayerAnswerServiceImpl implements PlayerAnswerService {

    private final PlayerAnswerRepository playerAnswerRepository;
    private final QuestionRepository questionRepository;
    private final PlayerRepository playerRepository;

    @Override
    public PageResult<PlayerAnswerDto> queryAll(PlayerAnswerDto criteria, Pageable pageable) {
        Page<PlayerAnswer> page = playerAnswerRepository.findAll((root, query, cb) -> 
                QueryHelp.getPredicate(root, criteria, cb), pageable);
        return PageUtil.toPage(page.map(this::toDto));
    }

    @Override
    public List<PlayerAnswerDto> getByPlayerId(Long playerId) {
        return playerAnswerRepository.findByPlayerId(playerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlayerAnswerDto> getByPlayerIdAndCategory(Long playerId, String category) {
        return playerAnswerRepository.findByPlayerIdAndQuestionCategory(playerId, category).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PlayerAnswerDto> submitSelfAssessment(List<PlayerAnswerDto> answers) {
        if (answers.isEmpty()) {
            throw new BadRequestException("No answers provided");
        }
        
        Long playerId = answers.get(0).getPlayerId();
        if (!playerRepository.existsById(playerId)) {
            throw new BadRequestException("Player not found");
        }
        
        // Delete existing answers if any
        List<PlayerAnswer> existingAnswers = playerAnswerRepository.findByPlayerId(playerId);
        if (!existingAnswers.isEmpty()) {
            playerAnswerRepository.deleteAll(existingAnswers);
        }
        
        List<PlayerAnswer> savedAnswers = new ArrayList<>();
        for (PlayerAnswerDto answerDto : answers) {
            if (!questionRepository.existsById(answerDto.getQuestionId())) {
                throw new BadRequestException("Question with ID " + answerDto.getQuestionId() + " not found");
            }
            
            PlayerAnswer answer = new PlayerAnswer();
            answer.setPlayerId(playerId);
            answer.setQuestionId(answerDto.getQuestionId());
            answer.setAnswerValue(answerDto.getAnswerValue());
            savedAnswers.add(playerAnswerRepository.save(answer));
        }
        
        // Calculate and update player score based on answers
        updatePlayerScore(playerId);
        
        return savedAnswers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PlayerAnswerDto create(PlayerAnswerDto resources) {
        PlayerAnswer playerAnswer = new PlayerAnswer();
        playerAnswer.setPlayerId(resources.getPlayerId());
        playerAnswer.setQuestionId(resources.getQuestionId());
        playerAnswer.setAnswerValue(resources.getAnswerValue());
        return toDto(playerAnswerRepository.save(playerAnswer));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PlayerAnswerDto resources) {
        PlayerAnswer playerAnswer = playerAnswerRepository.findById(resources.getId())
                .orElseThrow(() -> new BadRequestException("PlayerAnswer not found"));
        playerAnswer.setPlayerId(resources.getPlayerId());
        playerAnswer.setQuestionId(resources.getQuestionId());
        playerAnswer.setAnswerValue(resources.getAnswerValue());
        playerAnswerRepository.save(playerAnswer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        playerAnswerRepository.deleteById(id);
    }

    @Override
    public PlayerAnswerDto findById(Long id) {
        Optional<PlayerAnswer> playerAnswer = playerAnswerRepository.findById(id);
        ValidationUtil.isNull(playerAnswer, "PlayerAnswer", "id", id);
        return toDto(playerAnswer.get());
    }

    @Override
    public boolean hasCompletedSelfAssessment(Long playerId) {
        long count = playerAnswerRepository.countByPlayerId(playerId);
        // Consider assessment complete if at least one answer exists
        // In a real implementation, you might want to check if all required questions are answered
        return count > 0;
    }

    private PlayerAnswerDto toDto(PlayerAnswer playerAnswer) {
        PlayerAnswerDto dto = new PlayerAnswerDto();
        dto.setId(playerAnswer.getId());
        dto.setPlayerId(playerAnswer.getPlayerId());
        dto.setQuestionId(playerAnswer.getQuestionId());
        dto.setAnswerValue(playerAnswer.getAnswerValue());
        dto.setCreateTime(playerAnswer.getCreateTime());
        dto.setUpdateTime(playerAnswer.getUpdateTime());
        
        // Load question details if available
        if (playerAnswer.getQuestion() != null) {
            dto.setQuestionText(playerAnswer.getQuestion().getText());
            dto.setQuestionCategory(playerAnswer.getQuestion().getCategory());
        } else {
            // Lazy load question details if not already loaded
            questionRepository.findById(playerAnswer.getQuestionId()).ifPresent(question -> {
                dto.setQuestionText(question.getText());
                dto.setQuestionCategory(question.getCategory());
            });
        }
        
        return dto;
    }
    
    /**
     * Calculate and update player score based on self-assessment answers
     * @param playerId The player ID
     */
    @Transactional
    private void updatePlayerScore(Long playerId) {
        List<PlayerAnswer> answers = playerAnswerRepository.findByPlayerId(playerId);
        if (answers.isEmpty()) {
            return;
        }
        
        // Simple algorithm: average of all answer values
        double totalScore = answers.stream()
                .mapToInt(PlayerAnswer::getAnswerValue)
                .average()
                .orElse(0.0);
        
        // Update player score
        playerRepository.findById(playerId).ifPresent(player -> {
            player.setRateScore(totalScore);
            playerRepository.save(player);
        });
    }
}
