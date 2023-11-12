package edu.sru.thangiah.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examName;
    private int durationInMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    
    @Transient  // This annotation makes sure the field is not persisted in the database
    private String formattedStartTime;
    

    public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	@ManyToMany
    private List<ExamQuestion> questions;

    public Exam() {
        this.startTime = LocalDateTime.now(); // Sets the startTime to the current date and time.
    }
    
 // Call this method to format the startTime when setting it or after retrieving from the database
    public void formatStartTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.formattedStartTime = this.startTime.format(formatter);
    }

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

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public String getFormattedStartTime() {
		return formattedStartTime;
	}

	public void setFormattedStartTime(String formattedStartTime) {
		this.formattedStartTime = formattedStartTime;
	}
	
	
    // Getters and setters
    
    
}
