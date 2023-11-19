package edu.sru.thangiah.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.User;


public interface StudentRepository extends JpaRepository<Student, Long> {
	Optional<Student> findByStudentUsername(String username);

	List<Student> findBystudentFirstNameContaining(String name);

	List<Student> findBystudentUsernameContaining(String studentUsername);
	
	Optional<Student> findByUserId(User user);
	
    @Query("SELECT s FROM Student s WHERE s.user.id = :userId")
    Optional<Student> findByUserId(@Param("userId") Long userId);

	List<Student> findAllByCoursesIn(List<Course> courses);


	
	
}

