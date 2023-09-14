package edu.sru.thangiah.repository;


import org.springframework.data.repository.CrudRepository;

import edu.sru.thangiah.domain.Instructor;



public interface InstructorRepository extends CrudRepository<Instructor, Long> {
	Optional<Instructor> findByInstructorUsername(String username);

}

