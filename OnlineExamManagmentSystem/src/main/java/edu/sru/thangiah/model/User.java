package edu.sru.thangiah.model;

import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import java.util.stream.Collectors;

import java.util.UUID;


import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity

@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class User implements UserDetails{
	
	  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "first_name")
    private String firstName;

    @NonNull
    @Column(name = "last_name")
    private String lastName;

    @NonNull
    @Column(name = "email")
    private String email;

    @NonNull
    @Column(name = "password")
    private String password;

    @NonNull
    @Column(name = "username")
    private String username;

    @NonNull
    @Column(name = "verification_code")
    private String verificationCode;

    
    private boolean enabled;

	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", 
               joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), 
               inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Roles> roles = new ArrayList<>();
	

	public User(Long id, String firstName, String lastName, String email, String password, String username,
			String verificationCode, boolean enabled, List<Roles> roles) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.username = username;
		this.verificationCode = verificationCode;
		this.enabled = enabled;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}


    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }



	public List<Roles> getRoles() {
		return roles;
	}

	public void setRoles(List<Roles> roles) {
		this.roles = roles;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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


	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	public User(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Collection<Roles> getRoles() {
        return roles;
    }


    public void setRoles(Collection<Roles> roles) {
        this.roles = roles;
    }
}
