package edu.sru.thangiah.oems.domain;


import org.springframework.lang.NonNull;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Administrator {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @NonNull
    private String name;
    
    @NonNull
    private String email;
    
    @NonNull
    private String password;
    
    

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	

    // standard constructors / setters / getters / toString
}
/* Java Persistence is the API for the management for persistence and object/relational mapping.   */
/* https://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html#package.description */
/* */
/* */
package edu.sru.thangiah.oems.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.lang.NonNull;

@Entity
public class Administrator {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long AdministratorID;
    @NonNull
    private String AdministratorUsername;
    @NonNull
    private String AdministratorPassword;
    @NonNull
    private String FirstName;
    @NonNull
    private String LastName;
    
    //Getter for AdministratorID
	public Long getAdministratorID() {
		return AdministratorID;
	}
	//Setter for AdministratorID
	public void setAdministratorID(Long administratorID) {
		AdministratorID = administratorID;
	}
	
	//Getter for AdministratorUsername
	public String getAdministratorUsername() {
		return AdministratorUsername;
	}
	//Setter for AdministratorUsername
	public void setAdministratorUsername(String administratorUsername) {
		AdministratorUsername = administratorUsername;
	}
	
	//Getter for AdministratorPassword
	public String getAdministratorPassword() {
		return AdministratorPassword;
	}
	//Setter for AdministratorPassword
	public void setAdministratorPassword(String administratorPassword) {
		AdministratorPassword = administratorPassword;
	}
	
	//Getter for First Name
	public String getFirstName() {
		return FirstName;
	}
	//Setter for First Name 
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	
	//Getter for Last Name
	public String getLastName() {
		return LastName;
	}
	//Getter for Last Name
	public void setLastName(String lastName) {
		LastName = lastName;
	}
    

    // Constructors, getters, and setters, will eventually add more 
}
