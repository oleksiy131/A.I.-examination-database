package edu.sru.thangiah.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import edu.sru.thangiah.domain.Student;



public interface StudentRepository extends JpaRepository<Student, Long> {
	
	List<Student>findBystudentFirstNameContaining(String name);
}