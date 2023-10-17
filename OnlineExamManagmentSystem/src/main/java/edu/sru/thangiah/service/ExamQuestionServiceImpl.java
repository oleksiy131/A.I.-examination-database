package edu.sru.thangiah.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.repository.ExamQuestionRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class ExamQuestionServiceImpl implements ExamQuestionService {

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Override
    public void readExamQuestionsFromFile() throws IOException {
        // Define the path to your text file
        String filePath = "/static/chapter-1.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            ExamQuestion currentExamQuestion = null;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    // Skip empty lines
                    continue;
                }

                // Check if the line starts with a number (question number)
                if (Character.isDigit(line.charAt(0))) {
                    if (currentExamQuestion != null) {
                        // Save the previous exam question
                        examQuestionRepository.save(currentExamQuestion);
                    }
                    currentExamQuestion = new ExamQuestion();
                    String[] parts = line.split("\\.", 2);
                    currentExamQuestion.setQuestionText(parts[1].trim());
                } else if (currentExamQuestion != null) {
                    // Parse answer options and correct answer
                    if (line.startsWith("A.")) {
                        currentExamQuestion.setOptionA(line.substring(2).trim());
                    } else if (line.startsWith("B.")) {
                        currentExamQuestion.setOptionB(line.substring(2).trim());
                    } else if (line.startsWith("C.")) {
                        currentExamQuestion.setOptionC(line.substring(2).trim());
                    } else if (line.startsWith("D.")) {
                        currentExamQuestion.setOptionD(line.substring(2).trim());
                    } else if (line.startsWith("Ans:")) {
                        currentExamQuestion.setCorrectAnswer(line.substring(4).trim());
                    }
                }
            }

            // Save the last exam question after reading the file
            if (currentExamQuestion != null) {
                examQuestionRepository.save(currentExamQuestion);
            }
        }
    }

    @Override
    public List<ExamQuestion> getAllExamQuestions() {
        return examQuestionRepository.findAll();
    }

    @Override
    public ExamQuestion getExamQuestionById(Long id) {
        return examQuestionRepository.findById(id).orElse(null); 
    }
    @Override
    public void deleteExamQuestion(Long id) {
        examQuestionRepository.deleteById(id);
    }

	@Override
	public void saveExamQuestion(ExamQuestion question) {
		// TODO Auto-generated method stub
		
	}

}
