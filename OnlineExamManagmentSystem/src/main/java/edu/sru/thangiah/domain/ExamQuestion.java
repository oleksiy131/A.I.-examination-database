package edu.sru.thangiah.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity 
public class ExamQuestion {
	
	 public enum QuestionType {
	        MULTIPLE_CHOICE,
	        TRUE_FALSE,
	        FILL_IN_THE_BLANK
	    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private boolean isAiGenerated = false;

    @Column(columnDefinition = "TEXT")
    private String questionText;
    
    @Transient // This annotation means the field won't be persisted in the database
    private String userAnswer;
    
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private int chapter;
    private QuestionType questionType; 
    
    //This is for debugging...
    @Override
    public String toString() {
        return "ExamQuestion{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", optionA='" + optionA + '\'' +
                ", optionB='" + optionB + '\'' +
                ", optionC='" + optionC + '\'' +
                ", optionD='" + optionD + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", chapter=" + chapter +
                ", questionType=" + questionType +
                '}';
    }
    
 // Method to return correct answer text based on question type
    public String getCorrectAnswerText() {
        switch (this.questionType) {
            case MULTIPLE_CHOICE:
            	switch (this.correctAnswer) {
                case "A":
                    return this.optionA;
                case "B":
                    return this.optionB;
                case "C":
                    return this.optionC;
                case "D":
                    return this.optionD;
                default:
                    return "Invalid answer";
            }
            case TRUE_FALSE:
                return this.correctAnswer.equals("A") ? "True" : "False";
            case FILL_IN_THE_BLANK:
                return this.correctAnswer; 
            default:
                return "Invalid Answer";
        }
    }

    // Helper method to get the option text based on the option letter (A, B, C, D)
    private String getOptionText(String optionLetter) {
        switch (optionLetter) {
            case "A": return this.optionA;
            case "B": return this.optionB;
            case "C": return this.optionC;
            case "D": return this.optionD;
            default: return "Invalid Option";
        }
    }

    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public String getOptionA() {
		return optionA;
	}
	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}
	public String getOptionB() {
		return optionB;
	}
	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}
	public String getOptionC() {
		return optionC;
	}
	public void setOptionC(String optionC) {
		this.optionC = optionC;
	}
	public String getOptionD() {
		return optionD;
	}
	public void setOptionD(String optionD) {
		this.optionD = optionD;
	}
	public String getCorrectAnswer() {
		return correctAnswer;
	}
	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	public int getChapter() {
		return chapter;
	}
	public void setChapter(int chapter) {
		this.chapter = chapter;
	}
	public String getUserAnswer() {
		return userAnswer;
	}
	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	} 
	
	public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }


	public boolean isAiGenerated() {
		return isAiGenerated;
	}


	public void setAiGenerated(boolean isAiGenerated) {
		this.isAiGenerated = isAiGenerated;
	}
	
	
    
    
    
}