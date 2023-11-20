package edu.sru.thangiah.web.dto;

import java.util.ArrayList;
import java.util.List;

public class CourseGradeDTO {
    private String courseName;
    private List<ExamGradeDTO> examGrades = new ArrayList<>();

   

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
}
