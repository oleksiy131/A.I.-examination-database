package edu.sru.thangiah.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.sru.thangiah.domain.ExamQuestion;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
	ExamQuestion findByQuestionText(String questionText);
    List<ExamQuestion> findByChapter(int chapter);
    
    @Query("SELECT DISTINCT eq.chapter FROM ExamQuestion eq")
    List<Integer> findAllDistinctChapters();
    
    List<ExamQuestion> findQuestionsByChapter(int chapter);
	void save(List<ExamQuestion> aiQuestions);
    List<ExamQuestion> findByQuestionTextContainingIgnoreCase(String searchText);
    Optional<ExamQuestion> findById(Long id);
    
    @Query("SELECT q FROM ExamQuestion q WHERE q.questionType = ?1 ORDER BY FUNCTION('RAND')")
    List<ExamQuestion> findRandomQuestionsByType(ExamQuestion.QuestionType type, Pageable pageable);

}
