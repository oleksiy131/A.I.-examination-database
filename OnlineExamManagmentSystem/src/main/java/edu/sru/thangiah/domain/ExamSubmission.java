package edu.sru.thangiah.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*____  __    __        _ _ 
 / __ \/ /__ / /__ ___ (_|_)
/ /_/ / / -_)  '_/(_-</ / / 
\____/_/\__/_/\_\/___/_/_/  
                      
*/

public class ExamSubmission {

    private Long userId; 
    private Long examId; 
    private List<String> answers;
    private int score;


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
