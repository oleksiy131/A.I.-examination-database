package edu.sru.thangiah.domain;


import java.util.List;

import org.springframework.lang.NonNull;

import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "administrator", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Administrator {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long adminId;
    
    @NonNull
    @Column(name = "first_name")
    private String adminFirstName;
    
    @NonNull
    @Column(name = "last_name")
    private String adminLastName;
    
    @NonNull
    @Column(name = "email")
    private String adminEmail;
    
    @NonNull
    @Column(name = "password")
    private String adminPassword;
    
    @NonNull
    @Column(name = "username")
    private String adminUsername;
    
    @ManyToMany
    @JoinColumn(name = "role_id")
    private List<Roles> roles;
    
    

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
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

	

	public List<Roles> getRoles() {
		return roles;
	}

	public void setRoles(List<Roles> roles) {
		this.roles = roles;
	}

	public void setRoot(User root) {
	    this.adminFirstName = root.getFirstName();
	    this.adminLastName = root.getLastName();
	    this.adminEmail = root.getEmail();
	    this.adminPassword = root.getPassword();
	    this.adminUsername = root.getUsername();
	    this.roles = root.getRoles();
	}

	public void setUser(User administrator) {
		this.adminId = administrator.getId();
		this.adminFirstName = administrator.getFirstName();
		this.adminLastName = administrator.getLastName();
		this.adminEmail = administrator.getEmail();
		this.adminPassword = administrator.getPassword();
		this.adminUsername = administrator.getUsername();
		this.roles = administrator.getRoles();
	}

	public Administrator() {}


	public Administrator(Long adminId, String adminFirstName, String adminLastName, String adminEmail,
			String adminPassword, String adminUsername, List<Roles> roles) {
		super();
		this.adminId = adminId;
		this.adminFirstName = adminFirstName;
		this.adminLastName = adminLastName;
		this.adminEmail = adminEmail;
		this.adminPassword = adminPassword;
		this.adminUsername = adminUsername;
		this.roles = roles;
	}

	
	
	

    // standard constructors / setters / getters / toString
}
/* Java Persistence is the API for the management for persistence and object/relational mapping.   */
/* https://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html#package.description */
/* */
/* */
