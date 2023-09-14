package edu.sru.thangiah.repository;

import org.springframework.data.repository.CrudRepository;

import edu.sru.thangiah.domain.Student;


public interface StudentRepository extends CrudRepository<Student, Long> {
	Optional<Student> findByStudentUsername(String username);

