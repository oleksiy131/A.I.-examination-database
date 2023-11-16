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



	public void addExamGrade(String examName, Integer score) {
        this.examGrades.add(new ExamGradeDTO(examName, score));
    }
}
