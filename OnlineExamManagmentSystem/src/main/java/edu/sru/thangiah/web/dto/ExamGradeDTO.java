package edu.sru.thangiah.web.dto;

public class ExamGradeDTO {
    private String examName;
    private Integer score; // Use Integer to handle null values if there's no submission
    private Integer totalQuestions; // Total number of questions for the exam

    public ExamGradeDTO(String examName, Integer score, Integer totalQuestions) {
        this.examName = examName;
        this.score = score;
        this.totalQuestions = totalQuestions;
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
	
	public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getFormattedScore() {
        if (score == null || totalQuestions == null) {
            return "Not available";
        }
        return String.format("%d/%d", score, totalQuestions);
    }
    
 // Method to calculate the letter grade
    public String getLetterGrade() {
        if (score == null || totalQuestions == null || totalQuestions == 0) {
            return "N/A"; // Not Available or Not Applicable
        }
        double percentage = (double) score / totalQuestions * 100;
        if (percentage >= 90) {
            return "A";
        } else if (percentage >= 80) {
            return "B";
        } else if (percentage >= 70) {
            return "C";
        } else if (percentage >= 60) {
            return "D";
        } else {
            return "F";
        }
    }
    
    public String getPercentage() {
        if (score == null || totalQuestions == null || totalQuestions == 0) {
            return "Not available";
        }
        int percentage = (int) ((double) score / totalQuestions * 100);
        return percentage + "%";
    }

    
}
