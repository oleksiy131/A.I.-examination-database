package edu.sru.thangiah.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;



public interface CourseRepository extends JpaRepository<Course, Long> {
	
	List<Course> findByIdContaining(Long Id);
	
	@Query("SELECT c FROM Course c WHERE c.instructor = :instructor")
    List<Course> findAllByInstructor(@Param("instructor") Instructor instructor);
}