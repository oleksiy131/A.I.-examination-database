package edu.sru.thangiah.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.repository.ExamQuestionRepository;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.core.io.Resource;


/*____  __    __        _ _ 
 / __ \/ /__ / /__ ___ (_|_)
/ /_/ / / -_)  '_/(_-</ / / 
\____/_/\__/_/\_\/___/_/_/  
                       
*/

@Service
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ResourceLoader resourceLoader;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    public ExamQuestionServiceImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // Load questions from the text file only once when the application starts
    @PostConstruct
    public void initializeQuestions() {
        try {
            List<ExamQuestion> existingQuestions = examQuestionRepository.findAll();
            if (existingQuestions.isEmpty()) {
                loadQuestionsFromFile();
            }
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }
    }

    private void loadQuestionsFromFile() throws IOException {
        try {
            Resource resource = resourceLoader.getResource("classpath:static/chapter-1.txt");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Regular expression to identify the question number and text
            Pattern questionPattern = Pattern.compile("^(\\d+)\\.(.*)");

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }

                Matcher matcher = questionPattern.matcher(line);
                if (matcher.matches()) {
                    // This line is the start of a new question
                    ExamQuestion currentExamQuestion = new ExamQuestion();
                    currentExamQuestion.setQuestionText(matcher.group(2).trim()); // Group 2 is the question text

                    // We are within a question - parse answer options and correct answer
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
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

                    // Save the current exam question to the repository
                    examQuestionRepository.save(currentExamQuestion);
                }
            }
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
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
        examQuestionRepository.save(question); // Save the updated question to the repository
    }

	@Override
	public void readExamQuestionsFromFile() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
