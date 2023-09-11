package edu.sru.thangiah.domain;


import javax.persistence.JoinTable;

import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.persistence.JoinColumn;



@Entity
public class Instructor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long instructorId;
    
    @NonNull
    private LinkedList<Course> teachingCourses = new LinkedList<>();
    
    @NonNull
    @Column(name = "first_name")
    private String instructorFirstName;
    
    @NonNull
    @Column(name = "last_name")
    private String instructorLastName;
    
    @NonNull
    @Column(name = "email")
    private String instructorEmail;
    
    @NonNull
    @Column(name = "password")
    private String instructorPassword;
    
    @NonNull 
    @Column(name = "username")
    private String instructorUsername;
    
    @NonNull 
    @Column(name = "credits_taught")
    private float creditsTaught;

	public long getInstructorId() {
		return instructorId;
	}

	public void setInstructorId(long instructorId) {
		this.instructorId = instructorId;
	}

	public String getInstructorFirstName() {
		return instructorFirstName;
	}

	public void setInstructorFirstName(String instructorFirstName) {
		this.instructorFirstName = instructorFirstName;
	}

	public String getInstructorLastName() {
		return instructorLastName;
	}

	public void setInstructorLastName(String instructorLastName) {
		this.instructorLastName = instructorLastName;
	}

	public String getInstructorEmail() {
		return instructorEmail;
	}

	public void setInstructorEmail(String instructorEmail) {
		this.instructorEmail = instructorEmail;
	}

	public String getInstructorPassword() {
		return instructorPassword;
	}

	public void setInstructorPassword(String instructorPassword) {
		this.instructorPassword = instructorPassword;
	}

	public String getInstructorUsername() {
		return instructorUsername;
	}

	public void setInstructorUsername(String instructorUsername) {
		this.instructorUsername = instructorUsername;
	}

	public float getCreditsTaught() {
		return creditsTaught;
	}

	public void setCreditsTaught(float creditsTaught) {
		this.creditsTaught = creditsTaught;
	}
    
	
	
    // standard constructors / setters / getters / toString
}
