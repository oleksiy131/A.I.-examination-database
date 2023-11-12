package edu.sru.thangiah.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {

	Set<Exam> findByCourse(Course course);
	}
