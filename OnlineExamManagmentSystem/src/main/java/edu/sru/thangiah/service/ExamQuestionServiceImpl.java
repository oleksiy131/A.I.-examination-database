package edu.sru.thangiah.service;

import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.repository.ExamQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ResourceLoader resourceLoader;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    public ExamQuestionServiceImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // Load questions from the text files only once when the application starts.
    @PostConstruct
    public void initializeQuestions() {
        for (int chapter = 1; chapter <= 9; chapter++) { // assuming there are 9 chapters
            try {
                loadQuestionsFromFile(chapter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
    
    // Method to read and parse fill-in-the-blank questions when the 'Generate Exam' is clicked
    @Transactional
    public List<ExamQuestion> generateFillInTheBlanksQuestions(int numberOfQuestions) throws IOException {
        List<ExamQuestion> blanksQuestions = readBlanksFromFile();
        return blanksQuestions.subList(0, Math.min(numberOfQuestions, blanksQuestions.size()));
    }


    private void loadQuestionsFromFile(int chapter) throws IOException {
        String filePath = "classpath:static/chapter-" + chapter + ".txt";
        Resource resource = resourceLoader.getResource(filePath);
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
                currentExamQuestion.setChapter(chapter); // setting the chapter number for the question
                examQuestionRepository.save(currentExamQuestion);
                System.out.println("Saved Question: " + currentExamQuestion);
            }
        }
        reader.close();
    }
    
    public List<ExamQuestion> readBlanksFromFile() throws IOException {        
    	String filePath = "classpath:static/Blanks.txt";
        InputStream inputStream = resourceLoader.getResource(filePath).getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<ExamQuestion> blanksQuestions = new ArrayList<>();

        Pattern pattern = Pattern.compile("^(\\d+)\\. (.*)");
        String line;
        ExamQuestion question = null;

        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.matches()) {
                if (question != null) {
                    examQuestionRepository.save(question);
                    blanksQuestions.add(question);
                }
                question = new ExamQuestion();
                question.setQuestionText(matcher.group(2));
            } else if (line.startsWith("Ans:")) {
                if (question != null) {
                    question.setCorrectAnswer(line.substring(4).trim());
                }
            }
        }

        if (question != null) {
            examQuestionRepository.save(question);
            blanksQuestions.add(question);
        }

        reader.close();
        return blanksQuestions;
    }
    
    public List<ExamQuestion> readTrueFalseFromFile() throws IOException {
        String filePath = "classpath:static/true-false.txt";
        Resource resource = resourceLoader.getResource(filePath);
        InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<ExamQuestion> trueFalseQuestions = new ArrayList<>();

        // Regular expression to identify the question number and text
        Pattern questionPattern = Pattern.compile("^(\\d+)\\.\\s+(.*)$");
        // This pattern matches "Ans: A" or "Ans: B"
        Pattern answerPattern = Pattern.compile("^Ans:\\s+([AB])$");

        String line;
        ExamQuestion question = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            Matcher questionMatcher = questionPattern.matcher(line);

            if (questionMatcher.matches()) {
                if (question != null) {
                    // Save the previous question
                    examQuestionRepository.save(question);
                    trueFalseQuestions.add(question);
                }
                // Start a new question
                question = new ExamQuestion();
                question.setQuestionText(questionMatcher.group(2));
            } else {
                Matcher answerMatcher = answerPattern.matcher(line);
                if (answerMatcher.matches()) {
                    // This is the answer line
                    if (question != null) {
                        question.setCorrectAnswer(answerMatcher.group(1)); // Captures "A" or "B"
                    }
                }
            }
        }

        // Save the last question if there is one
        if (question != null) {
            examQuestionRepository.save(question);
            trueFalseQuestions.add(question);
        }

        reader.close();
        return trueFalseQuestions;
    }


    public List<Integer> getAllChapters() {
        return examQuestionRepository.findAllDistinctChapters();
    }
    
    public List<ExamQuestion> generateQuestionsForChapter(int chapter) {
        return examQuestionRepository.findQuestionsByChapter(chapter);
    }


	 @Override
	    public List<ExamQuestion> getQuestionsByChapter(int chapter) {
	        return examQuestionRepository.findByChapter(chapter);
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
	 public void saveExamQuestion(ExamQuestion examQuestion) {
	     examQuestionRepository.save(examQuestion);
	 }

	 @Override
	 public void deleteExamQuestion(Long id) {
	     examQuestionRepository.deleteById(id);
	 }

	@Override
	public void readExamQuestionsFromFile() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
