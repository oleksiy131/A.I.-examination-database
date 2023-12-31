package edu.sru.thangiah.domain;


import java.util.List;
import java.util.Set;

import javax.persistence.JoinColumn;

import org.springframework.lang.NonNull;

import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(name = "instructor", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Instructor {

    
    @Id
    @Column(name = "id")

    private Long instructorId;
    
    /*
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "INSTRUCTOR_COURSE",
        joinColumns = @JoinColumn(name = "instructor_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    */
    
    @OneToMany(mappedBy = "instructor", cascade = {}, orphanRemoval = false)

    private Set<Course> courses;
    
    @ManyToMany
    @JoinColumn(name = "role_id")
    private List<Roles> roles;
 
    
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
	
	

	public List<Roles> getRoles() {
		return roles;
	}

	public void setRoles(List<Roles> roles) {
		this.roles = roles;
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


	public Set<Course> getCourses() {
		return courses;
	}

	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}
	
	// Constructors
    public Instructor() {
    }

    public Instructor(String instructorFirstName, String instructorLastName, String instructorEmail,
                      String instructorPassword, String instructorUsername, float creditsTaught) {
        this.instructorFirstName = instructorFirstName;
        this.instructorLastName = instructorLastName;
        this.instructorEmail = instructorEmail;
        this.instructorPassword = instructorPassword;
        this.instructorUsername = instructorUsername;
        this.creditsTaught = creditsTaught;
    }

	public void setInstructorId(Long instructorId) {
		this.instructorId = instructorId;
	}

	public void setUser(User instructor) {
	    this.instructorFirstName = instructor.getFirstName();
	    this.instructorLastName = instructor.getLastName();
	    this.instructorEmail = instructor.getEmail();
	    this.instructorPassword = instructor.getPassword();
	    this.instructorUsername = instructor.getUsername();
	    this.roles = instructor.getRoles();
	}
	
	
	
    // standard constructors / setters / getters / toString
}
