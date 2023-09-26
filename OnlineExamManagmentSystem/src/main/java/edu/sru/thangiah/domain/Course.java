package edu.sru.thangiah.domain;

import java.util.Set;

import javax.persistence.JoinColumn;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//course
@Entity
@Table(name = "COURSE")

public class Course {
	@Id
	private Long id;
    
    private String courseName;
    
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Student> students;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;


    // Add getters and setters
    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCourseName() {
		return courseName;
	}
	

	//public void setCourseName(String className) {
	//	this.courseName = className;
	//}
	
	public void setCourseName(String courseName) {
	    this.courseName = courseName;
	}
	

	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	
	
	
	
    
    
    
}