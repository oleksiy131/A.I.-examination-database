package edu.sru.thangiah.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.sru.thangiah.domain.ExamQuestion;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
	ExamQuestion findByQuestionText(String questionText);
    List<ExamQuestion> findByChapter(int chapter);
    
    @Query("SELECT DISTINCT eq.chapter FROM ExamQuestion eq")
    List<Integer> findAllDistinctChapters();
    
    List<ExamQuestion> findQuestionsByChapter(int chapter);

}
