package edu.sru.thangiah.web.dto;

public class ExamGradeDTO {
    private String examName;
    private Integer score; // Use Integer to handle null values if there's no submission

    public ExamGradeDTO(String examName, Integer score) {
        this.examName = examName;
        this.score = score;
    }

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

    
}
