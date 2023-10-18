package edu.sru.thangiah.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;public class ExamSubmission {

    private Long userId; // Add userId property
    private Long examId; // Add examId property
    private List<String> answers;
    private int score;

    // Constructors, getters, and setters

    public ExamSubmission() {
        // Default constructor
    }

    public ExamSubmission(Long userId, Long examId, List<String> answers, int score) {
        this.userId = userId;
        this.examId = examId;
        this.answers = answers;
        this.score = score;
    }
    
    public ExamSubmission(Long userId, Long examId, Map<Long, String> answers, int score) {
        this.userId = userId;
        this.examId = examId;
        this.answers = new ArrayList<>(answers.values()); // Convert Map values to List
        this.score = score;
    }

    // Add getters and setters for userId and examId
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    // Placeholder method to calculate the score
    public int calculateScore(Exam exam, Map<Long, String> userAnswers) {
        int score = 0;
        List<ExamQuestion> questions = exam.getQuestions();

        // Loop through the questions and compare user answers with correct answers
        for (ExamQuestion question : questions) {
            Long questionId = question.getId();
            String correctAnswer = question.getCorrectAnswer();
            String userAnswer = userAnswers.get(questionId);

            if (userAnswer != null && userAnswer.equals(correctAnswer)) {
                score++; // Increment the score for correct answers
            }
        }

        return score;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
