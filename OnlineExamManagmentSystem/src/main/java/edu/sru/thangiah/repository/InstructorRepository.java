package edu.sru.thangiah.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import edu.sru.thangiah.domain.Instructor;



public interface InstructorRepository extends JpaRepository<Instructor, Long> {
	Optional<Instructor> findByInstructorUsername(String username);

	List<Instructor> findByInstructorFirstNameContaining(String searchParam);

	List<Instructor> findByInstructorUsernameContaining(String searchParam);
	
	

}

