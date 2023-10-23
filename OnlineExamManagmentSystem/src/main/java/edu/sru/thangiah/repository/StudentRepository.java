package edu.sru.thangiah.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import edu.sru.thangiah.domain.Student;


public interface StudentRepository extends JpaRepository<Student, Long> {
	Optional<Student> findByStudentUsername(String username);

	List<Student> findBystudentFirstNameContaining(String name);

	List<Student> findBystudentUsernameContaining(String studentUsername);

}

