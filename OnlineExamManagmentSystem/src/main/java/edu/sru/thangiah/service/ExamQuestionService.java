package edu.sru.thangiah.service;

import java.io.IOException;
//ExamQuestionService.java
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.sru.thangiah.domain.ExamQuestion;

public interface ExamQuestionService {
    List<ExamQuestion> getAllExamQuestions();
    ExamQuestion getExamQuestionById(Long id);
    void saveExamQuestion(ExamQuestion examQuestion);
    void deleteExamQuestion(Long id);
    void readExamQuestionsFromFile() throws IOException;
    List<ExamQuestion> getQuestionsByChapter(int chapter);
    public List<ExamQuestion> generateQuestionsForChapter(int chapter);
    public List<Integer> getAllChapters();
    List<ExamQuestion> readBlanksFromFile() throws IOException;
	List<ExamQuestion> generateFillInTheBlanksQuestions(int numBlanks) throws IOException;
	List<ExamQuestion> readTrueFalseFromFile() throws IOException;
    List<ExamQuestion> readAIQuestionsFromFile(MultipartFile file) throws IOException;
	List<ExamQuestion> findQuestionsContainingText(String searchText);
}

