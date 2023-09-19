package edu.sru.thangiah.domain;


import org.springframework.lang.NonNull; 

import edu.sru.thangiah.model.User.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "administrator", uniqueConstraints = @UniqueConstraint(columnNames = "adminId"))
public class Administrator {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long adminId;
    
    @NonNull
    @Column (name = "first_name")
    private String adminFirstName;
    
    @NonNull
    @Column (name = "last_name")
    private String adminLastName;
    
    @NonNull
    @Column (name = "email")
    private String adminEmail;
    
    @NonNull
    @Column (name = "password")
    private String adminPassword;
    
    @NonNull
    @Column (name = "username")
    private String adminUsername;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
   
	public Administrator(long adminId, String adminFirstName, String adminLastName, String adminEmail,
			String adminPassword, String adminUsername, Role role) {
		super();
		this.adminId = adminId;
		this.adminFirstName = adminFirstName;
		this.adminLastName = adminLastName;
		this.adminEmail = adminEmail;
		this.adminPassword = adminPassword;
		this.adminUsername = adminUsername;
		this.role = role;
	}

	public long getAdminId() {
		return adminId;
	}

	public void setAdminId(long adminId) {
		this.adminId = adminId;
	}

	public String getAdminFirstName() {
		return adminFirstName;
	}

	public void setAdminFirstName(String adminFirstName) {
		this.adminFirstName = adminFirstName;
	}

	public String getAdminLastName() {
		return adminLastName;
	}

	public void setAdminLastName(String adminLastName) {
		this.adminLastName = adminLastName;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getAdminUsername() {
		return adminUsername;
	}

	public void setAdminUsername(String adminUsername) {
		this.adminUsername = adminUsername;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

    // standard constructors / setters / getters / toString
}
/* Java Persistence is the API for the management for persistence and object/relational mapping.   */
/* https://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html#package.description */
/* */
/* */
