package edu.sru.thangiah.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.sru.thangiah.domain.Student;


public interface StudentRepository extends CrudRepository<Student, Long> {
	Optional<Student> findByStudentUsername(String username);

	Optional<Student> findBystudentFirstNameContaining(String name);
}

