/* Seth Chritzman 																				   */
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
    
    // Getter for Administrator ID
    public Long getAdministratorID() {
		return AdministratorID;
			
	}
    
    // Setter for Administrator ID
	public void setAdministratorID(Long AdministratorID) {
		this.AdministratorID = AdministratorID;
	}
	
    // Getter for Administrator Username
	public String getAdministratorUsername() {
		return AdministratorUsername;
	}
	
    // Setter for Administrator Username
	public void setAdministratorUsernamee(String AdministratorUsername) {
		this.AdministratorUsername = AdministratorUsername;
	}
    // Getter for Administrator Password
	public String getAdministratorPassword() {
		return AdministratorPassword;
	}
    // Setter for Administrator Password
	public void setAdministratorPassword(String AdministratorPassword) {
		this.AdministratorPassword = AdministratorPassword;
	}

    // Constructors, getters, and setters, will eventually add more 
}
