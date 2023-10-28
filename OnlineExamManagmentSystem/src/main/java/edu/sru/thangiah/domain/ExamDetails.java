package edu.sru.thangiah.domain;

import java.util.List;

public class ExamDetails {
    private List<Long> selectedExamQuestionIds;
    private String examName;
    private int durationInMinutes; // Duration in minutes
    private int chapter;
    
    
	public List<Long> getSelectedExamQuestionIds() {
		return selectedExamQuestionIds;
	}
	public void setSelectedExamQuestionIds(List<Long> selectedExamQuestionIds) {
		this.selectedExamQuestionIds = selectedExamQuestionIds;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public int getDurationInMinutes() {
		return durationInMinutes;
	}
	public void setDurationInMinutes(int examDuration) {
		this.durationInMinutes = examDuration;
	}
	public int getChapter() {
		return chapter;
	}
	public void setChapter(int chapter) {
		this.chapter = chapter;
	}
	

    
    
}