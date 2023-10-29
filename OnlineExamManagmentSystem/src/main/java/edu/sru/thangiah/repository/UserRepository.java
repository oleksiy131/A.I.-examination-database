package edu.sru.thangiah.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

	
	//List<User> findByUserFirstNameContaining(String name);


    // Custom query method to find a user by verification code
    User findByVerificationCode(String verificationCode);




}
