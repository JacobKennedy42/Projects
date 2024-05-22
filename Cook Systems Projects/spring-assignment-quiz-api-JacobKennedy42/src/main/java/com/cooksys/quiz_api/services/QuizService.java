package com.cooksys.quiz_api.services;

import java.util.List;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;

public interface QuizService {

  List<QuizResponseDto> getAllQuizzes();
  QuizResponseDto postQuiz(QuizRequestDto request);
  QuizResponseDto renameQuiz(Long id, String newName);
  QuizResponseDto deleteQuiz(Long id);
  QuestionResponseDto randomQuestion(Long id);
  QuizResponseDto addQuestion(Long id, QuestionRequestDto request);
  QuestionResponseDto deleteQuestion (Long questionId);
}
