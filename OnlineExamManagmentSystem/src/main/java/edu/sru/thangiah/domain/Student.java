package edu.sru.thangiah.domain;


import org.springframework.lang.NonNull;

import edu.sru.thangiah.model.Roles;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "student", uniqueConstraints = @UniqueConstraint(columnNames = "studentId"))
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long studentId;
    
    @NonNull
    @Column (name = "first_name")
    private String studentFirstName;
    
    @NonNull
    @Column (name = "last_name")
    private String studentLastName;
    
    @NonNull
    @Column (name = "email")
    private String studentEmail;
    
    @NonNull
    @Column (name = "password")
    private String studentPassword;
    
    @NonNull
    @Column (name = "username")
    private String studentUsername;
    
    @NonNull
    @Column (name = "enrolled_credits")
    private float creditsTaken;
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;

    
    // standard constructors / setters / getters / toString

	public long getStudentId() {
		return studentId;
	}

	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}

	public String getStudentFirstName() {
		return studentFirstName;
	}

	public void setStudentFirstName(String studentFirstName) {
		this.studentFirstName = studentFirstName;
	}

	public String getStudentLastName() {
		return studentLastName;
	}

	public void setStudentLastName(String studentLastName) {
		this.studentLastName = studentLastName;
	}

	public String getStudentEmail() {
		return studentEmail;
	}

	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}

	public String getStudentPassword() {
		return studentPassword;
	}

	public void setStudentPassword(String studentPassword) {
		this.studentPassword = studentPassword;
	}

	public String getStudentUsername() {
		return studentUsername;
	}

	public void setStudentUsername(String studentUsername) {
		this.studentUsername = studentUsername;
	}

	public float getCreditsTaken() {
		return creditsTaken;
	}

	public void setCreditsTaken(float creditsTaken) {
		this.creditsTaken = creditsTaken;
	}
}
