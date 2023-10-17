package edu.sru.thangiah.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.sru.thangiah.domain.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {
}
