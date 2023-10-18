package edu.sru.thangiah.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.repository.ExamQuestionRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExamQuestionServiceImpl implements ExamQuestionService {

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Override
    public void readExamQuestionsFromFile() throws IOException {
        // Define the path to your text file
        String filePath = "/static/chapter-1.txt";

        // Regular expression to identify the question number and text
        Pattern questionPattern = Pattern.compile("^(\\d+)\\.(.*)");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            ExamQuestion currentExamQuestion = null;
            boolean isQuestion = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    // Empty line indicates the end of the current question
                    if (currentExamQuestion != null) {
                        // Save the previous exam question
                        examQuestionRepository.save(currentExamQuestion);
                        currentExamQuestion = null;
                        isQuestion = false;
                    }
                    continue;
                }

                Matcher matcher = questionPattern.matcher(line);
                if (matcher.matches()) {
                    // This line is the start of a new question
                    isQuestion = true;
                    currentExamQuestion = new ExamQuestion();
                    currentExamQuestion.setQuestionText(matcher.group(2).trim()); // Group 2 is the question text
                } else if (isQuestion) {
                    // We are within a question - parse answer options and correct answer
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

            // If the file didn't end with an empty line, we need to save the last question
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
