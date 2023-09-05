package edu.sru.thangiah.oems.repository;

import org.springframework.data.repository.CrudRepository;

import edu.sru.thangiah.oems.domain.Student;



public interface StudentRepository extends CrudRepository<Student, Long> {}