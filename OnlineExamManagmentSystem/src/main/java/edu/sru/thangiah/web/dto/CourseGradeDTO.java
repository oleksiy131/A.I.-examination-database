package edu.sru.thangiah.web.dto;

import java.util.ArrayList;
import java.util.List;

public class CourseGradeDTO {
    private String courseName;
    private List<ExamGradeDTO> examGrades = new ArrayList<>();
    
    private Integer totalScore = 0;
    private Integer totalPossibleScore = 0;
    private Double percentage = 0.0;

   

    public String getCourseName() {
		return courseName;
	}



	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}



	public List<ExamGradeDTO> getExamGrades() {
		return examGrades;
	}



	public void setExamGrades(List<ExamGradeDTO> examGrades) {
		this.examGrades = examGrades;
	}



	// Method to add an exam grade with exam name and score
    public void addExamGrade(String examName, Integer score, Integer totalQuestions) {
        this.examGrades.add(new ExamGradeDTO(examName, score, totalQuestions));
    }

    // Overloaded method to add an ExamGradeDTO object directly
    public void addExamGrade(ExamGradeDTO examGrade) {
        this.examGrades.add(examGrade);
    }
    
    // Call this method to update the total score and percentage after adding all exam grades
    public void calculateTotalScoreAndPercentage() {
        for (ExamGradeDTO examGrade : examGrades) {
            if (examGrade.getScore() != null) {
                totalScore += examGrade.getScore();
                totalPossibleScore += examGrade.getTotalQuestions();
            }
        }
        if (totalPossibleScore > 0) {
            percentage = (double) totalScore / totalPossibleScore * 100;
        }
    }



	public Integer getTotalScore() {
		return totalScore;
	}



	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}



	public Integer getTotalPossibleScore() {
		return totalPossibleScore;
	}



	public void setTotalPossibleScore(Integer totalPossibleScore) {
		this.totalPossibleScore = totalPossibleScore;
	}



	public Double getPercentage() {
		return percentage;
	}



	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
    
    

    
}
