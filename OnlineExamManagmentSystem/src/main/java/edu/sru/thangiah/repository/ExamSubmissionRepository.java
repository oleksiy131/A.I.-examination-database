package edu.sru.thangiah.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.sru.thangiah.domain.Exam;
import edu.sru.thangiah.domain.ExamSubmissionEntity;
import jakarta.transaction.Transactional;

public interface ExamSubmissionRepository extends JpaRepository<ExamSubmissionEntity, Long> {

    ExamSubmissionEntity findByUser_IdAndExam_Id(Long userId, Long examId);
    
    @Transactional
    void deleteByExam(Exam exam);
    
    long countByExam(Exam exam);

    boolean existsByUser_IdAndExam_Id(Long userId, Long examId);

}
