package edu.sru.thangiah.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.sru.thangiah.domain.Course;



public interface CourseRepository extends CrudRepository<Course, Long> {
	
	List<Course> findByCourseNameContaining(String name);
}