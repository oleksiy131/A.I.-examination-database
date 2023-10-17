package edu.sru.thangiah.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.sru.thangiah.domain.ExamQuestion;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
}
