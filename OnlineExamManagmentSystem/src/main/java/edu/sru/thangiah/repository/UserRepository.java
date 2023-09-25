package edu.sru.thangiah.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository; 
import edu.sru.thangiah.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

	
	// Custom query method to find a user by verification code
    User findByVerificationCode(String verificationCode);


}
