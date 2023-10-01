package edu.sru.thangiah.domain;

import org.springframework.lang.NonNull;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import edu.sru.thangiah.model.Roles;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "ScheduleManager", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class ScheduleManager {

	
	
	public ScheduleManager() {
		super();
	}

	public ScheduleManager(String managerFirstName, String managerLastName, String managerEmail,
			String managerPassword, String managerUsername) {
		super();
		this.managerFirstName = managerFirstName;
		this.managerLastName = managerLastName;
		this.managerEmail = managerEmail;
		this.managerPassword = managerPassword;
		this.managerUsername = managerUsername;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@NonNull
    @Column(name = "id")
    private Long managerId;
    
    @NonNull
    @Column(name = "first_name")
    private String managerFirstName;
    
    @NonNull
    @Column(name = "last_name")
    private String managerLastName;
    
    @NonNull
    @Column(name = "email")
    private String managerEmail;
    
    @NonNull
    @Column(name = "password")
    private String managerPassword;
    
    @NonNull
    @Column(name = "username")
    private String managerUsername;
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;
        

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public String getManagerFirstName() {
		return managerFirstName;
	}

	public void setManagerFirstName(String managerFirstName) {
		this.managerFirstName = managerFirstName;
	}

	public String getManagerLastName() {
		return managerLastName;
	}

	public void setManagerLastName(String managerLastName) {
		this.managerLastName = managerLastName;
	}

	public String getManagerEmail() {
		return managerEmail;
	}

	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}

	public String getManagerPassword() {
		return managerPassword;
	}

	public void setManagerPassword(String managerPassword) {
		this.managerPassword = managerPassword;
	}

	public String getManagerUsername() {
		return managerUsername;
	}

	public void setManagerUsername(String managerUsername) {
		this.managerUsername = managerUsername;
	}

	public Roles getRole() {
		return role;
	}
		
	public void setRole(Roles role) {
	    this.role = role;
	}
		


}