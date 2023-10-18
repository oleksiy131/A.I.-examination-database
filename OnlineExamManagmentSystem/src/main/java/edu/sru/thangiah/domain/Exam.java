package edu.sru.thangiah.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examName;
    private int durationInMinutes;
    private LocalDateTime startTime;

    public Exam() {
        this.startTime = LocalDateTime.now(); // Sets the startTime to the current date and time.
    }

    @ManyToMany
    private List<ExamQuestion> questions;
    
    // Getter and setter for startTime
    public LocalDateTime getStartTime() {
        return startTime;
    }
    

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public void setDurationInMinutes(int durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}

	public List<ExamQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<ExamQuestion> questions) {
		this.questions = questions;
	}

    // Getters and setters
    
    
}
