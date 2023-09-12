package edu.sru.thangiah.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import edu.sru.thangiah.domain.Instructor;



public interface InstructorRepository extends JpaRepository<Instructor, Long> {}