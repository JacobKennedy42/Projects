package com.cooksys.quiz_api.services.impl;

import java.util.List;
import java.util.Optional;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private final QuizRepository quizRepository;
  private final QuizMapper quizMapper;
  private final QuestionRepository questionRepository;
  private final QuestionMapper questionMapper;
  private final AnswerRepository answerRepository;

  private Quiz saveQuizInRepo (Quiz quiz) {
    Quiz savedQuiz = quizRepository.saveAndFlush(quiz);
    if (quiz.getQuestions() != null)
      for (Question question : quiz.getQuestions())
        saveQuestionInRepo(question, quiz);
    return savedQuiz;
  }
  private Question saveQuestionInRepo (Question question, Quiz parentQuiz) {
    question.setQuiz(parentQuiz);
    if (question.getQuiz() != null)
      question.setDeleted(parentQuiz.isDeleted());
    Question savedQuestion = questionRepository.saveAndFlush(question);
    if (question.getAnswers() != null)
      for (Answer answer : question.getAnswers())
        saveAnswerInRepo(answer, question);
    return savedQuestion;
  }
  private Answer saveAnswerInRepo (Answer answer, Question parentQuestion) {
    answer.setQuestion(parentQuestion);
    answer.setDeleted(parentQuestion.isDeleted());
    return answerRepository.saveAndFlush(answer);
  }

  private Quiz getQuiz (Long id) {
    Optional<Quiz> quiz = quizRepository.findByIdAndDeletedFalse(id);
    if (quiz.isEmpty())
      return null; // TODO: throw new NotFoundException("Quiz with id " + id + " not found"); watch video and make this
    return quiz.get();
  }

  private Question getRandomQuestionFrom (Quiz quiz) {
    List<Question> questions = questionRepository.findAllByQuizAndDeletedFalse(quiz);
    if (questions.size() <= 0)
      return null;  // TODO: throw new NotFoundException("Quiz with id " + id + " not found"); watch video and make this
    int randomIndex = (int) (Math.random() * questions.size());
    return quiz.getQuestions().get(randomIndex);
  }

  private Question getQuestion (Long id) {
    Optional<Question> question = questionRepository.findByIdAndDeletedFalse(id);
    if (question.isEmpty())
      return null; // TODO: throw new NotFoundException("Quiz with id " + id + " not found"); watch video and make this
    return question.get();
  }

  @Override
  public List<QuizResponseDto> getAllQuizzes() {
    return quizMapper.entitiesToDtos(quizRepository.findAllByDeletedFalse());
  }
  @Override
  public QuizResponseDto postQuiz(QuizRequestDto request) {
    Quiz quiz = quizMapper.dtoToEntity(request);
    quiz.setDeleted(false);
    return quizMapper.entityToDto(saveQuizInRepo(quiz));
  }

  @Override
  public QuizResponseDto renameQuiz (Long id, String newName){
    Quiz quiz = getQuiz(id);
    quiz.setName(newName);
    return quizMapper.entityToDto(saveQuizInRepo(quiz));
  }

  @Override
  public QuizResponseDto deleteQuiz (Long id) {
    Quiz quiz = getQuiz(id);
    quiz.setDeleted(true);
    return quizMapper.entityToDto(saveQuizInRepo(quiz));
  }

  @Override
  public QuestionResponseDto randomQuestion (Long id) {
    Quiz quiz = getQuiz(id);
    Question randomQuestion = getRandomQuestionFrom(quiz);
    return questionMapper.entityToDto(randomQuestion);
  }

  @Override
  public QuizResponseDto addQuestion (Long id, QuestionRequestDto request) {
    Quiz quiz = getQuiz(id);
    Question newQuestion = questionMapper.dtoToEntity(request);
    saveQuestionInRepo(newQuestion, quiz);
    return quizMapper.entityToDto(quiz);
  }

  @Override
  public QuestionResponseDto deleteQuestion (Long questionid) {
    Question question = getQuestion(questionid);
    question.setDeleted(true);
    return questionMapper.entityToDto(saveQuestionInRepo(question, null));
  } 
}
