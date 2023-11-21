package edu.sru.thangiah.service;

import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.domain.ExamQuestionDisplay;
import edu.sru.thangiah.repository.ExamQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        if (examQuestionRepository.count() == 0) { // Check if the database is empty
            // Load multiple-choice questions
            for (int chapter = 1; chapter <= 9; chapter++) {
                try {
                    loadQuestionsFromFile(chapter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Load true/false questions
            try {
                loadTrueFalseQuestions(); // Revised method to load once
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Load fill-in-the-blanks questions
            try {
                loadBlanksQuestions(); // Revised method to load once
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<ExamQuestion> getRandomTrueFalseQuestions(int numQuestions) {
        Pageable limit = PageRequest.of(0, numQuestions);
        return examQuestionRepository.findRandomQuestionsByType(ExamQuestion.QuestionType.TRUE_FALSE, limit);
    }

    public List<ExamQuestion> getRandomFillInTheBlanksQuestions(int numQuestions) {
        Pageable limit = PageRequest.of(0, numQuestions);
        return examQuestionRepository.findRandomQuestionsByType(ExamQuestion.QuestionType.FILL_IN_THE_BLANK, limit);
    }
    
    private void loadTrueFalseQuestions() throws IOException {
        String filePath = "classpath:chapters/true-false.txt";
        Resource resource = resourceLoader.getResource(filePath);
        InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Pattern questionPattern = Pattern.compile("^(\\d+)\\.\\s+(.*)$");
        Pattern answerPattern = Pattern.compile("^Ans:\\s+([AB])$");
        String line;
        ExamQuestion question = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            Matcher questionMatcher = questionPattern.matcher(line);
            if (questionMatcher.matches()) {
                question = new ExamQuestion();
                question.setQuestionText(questionMatcher.group(2));
                question.setQuestionType(ExamQuestion.QuestionType.TRUE_FALSE);
                question.setOptionA("True");
                question.setOptionB("False");
            } else if (question != null) {
                Matcher answerMatcher = answerPattern.matcher(line);
                if (answerMatcher.matches()) {
                    question.setCorrectAnswer(answerMatcher.group(1));
                    examQuestionRepository.save(question);
                }
            }
        }

        reader.close();
    }
    private void loadBlanksQuestions() throws IOException {
        String filePath = "classpath:chapters/Blanks.txt";
        InputStream inputStream = resourceLoader.getResource(filePath).getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Pattern pattern = Pattern.compile("^(\\d+)\\. (.*)");
        String line;
        ExamQuestion question = null;

        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.matches()) {
                question = new ExamQuestion();
                question.setQuestionText(matcher.group(2));
                question.setQuestionType(ExamQuestion.QuestionType.FILL_IN_THE_BLANK);
            } else if (line.startsWith("Ans:")) {
                if (question != null) {
                    question.setCorrectAnswer(line.substring(4).trim());
                    examQuestionRepository.save(question);
                }
            }
        }

        reader.close();
    }


    
    // Method to read and parse fill-in-the-blank questions when the 'Generate Exam' is clicked
    @Transactional
    public List<ExamQuestion> generateFillInTheBlanksQuestions(int numberOfQuestions) throws IOException {
        List<ExamQuestion> blanksQuestions = readBlanksFromFile();
        return blanksQuestions.subList(0, Math.min(numberOfQuestions, blanksQuestions.size()));
    }


    private void loadQuestionsFromFile(int chapter) throws IOException {
        String filePath = "classpath:chapters/chapter-" + chapter + ".txt";
        Resource resource = resourceLoader.getResource(filePath);
        InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Pattern questionPattern = Pattern.compile("^(\\d+)\\.(.*)");
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            Matcher matcher = questionPattern.matcher(line);
            if (matcher.matches()) {
                ExamQuestion currentExamQuestion = new ExamQuestion();
                currentExamQuestion.setQuestionText(matcher.group(2).trim());
                currentExamQuestion.setQuestionType(ExamQuestion.QuestionType.MULTIPLE_CHOICE);
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    if (line.startsWith("A.")) currentExamQuestion.setOptionA(line.substring(2).trim());
                    else if (line.startsWith("B.")) currentExamQuestion.setOptionB(line.substring(2).trim());
                    else if (line.startsWith("C.")) currentExamQuestion.setOptionC(line.substring(2).trim());
                    else if (line.startsWith("D.")) currentExamQuestion.setOptionD(line.substring(2).trim());
                    else if (line.startsWith("Ans:")) currentExamQuestion.setCorrectAnswer(line.substring(4).trim());
                }
                currentExamQuestion.setChapter(chapter);
                examQuestionRepository.save(currentExamQuestion);
            }
        }
        reader.close();
    }
    
    @Override
    @Transactional
    public List<ExamQuestion> readAIQuestionsFromFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot parse empty file");
        }

        List<ExamQuestion> aiQuestions = new ArrayList<>();
        Pattern questionPattern = Pattern.compile("^(\\d+)\\.\\s*(.*)");
        Pattern optionPattern = Pattern.compile("^([A-D])\\)\\s*(.*)"); 
        Pattern answerPattern = Pattern.compile("^Ans:\\s*([A-D])");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            ExamQuestion aiQuestion = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                Matcher questionMatcher = questionPattern.matcher(line);
                Matcher optionMatcher = optionPattern.matcher(line);
                Matcher answerMatcher = answerPattern.matcher(line);

                if (questionMatcher.matches()) {
                    // Save the previous question before starting a new one
                    if (aiQuestion != null) {
                        aiQuestions.add(aiQuestion);
                    }
                    aiQuestion = new ExamQuestion();
                    aiQuestion.setQuestionText(questionMatcher.group(2).trim());
                    System.out.println("Question text set.");
                    aiQuestion.setQuestionType(ExamQuestion.QuestionType.MULTIPLE_CHOICE);
                    aiQuestion.setAiGenerated(true);
                } else if (optionMatcher.matches() && aiQuestion != null) {
                    String optionLetter = optionMatcher.group(1);
                    String optionText = optionMatcher.group(2).trim();
                    // Set the option based on the letter
                    if (optionLetter.equalsIgnoreCase("A")) aiQuestion.setOptionA(optionText);
                    else if (optionLetter.equalsIgnoreCase("B")) aiQuestion.setOptionB(optionText);
                    else if (optionLetter.equalsIgnoreCase("C")) aiQuestion.setOptionC(optionText);
                    else if (optionLetter.equalsIgnoreCase("D")) aiQuestion.setOptionD(optionText);
                    System.out.println("Set A.B.C.D.");
                } else if (answerMatcher.matches() && aiQuestion != null) {
                    aiQuestion.setCorrectAnswer(answerMatcher.group(1));
                }
            }

            // Add the last question if it exists
            if (aiQuestion != null) {
                aiQuestions.add(aiQuestion);
            }

            // Save all questions to the repository at once
            examQuestionRepository.saveAll(aiQuestions);
        }

        return aiQuestions;
    }






    
    public List<ExamQuestion> readBlanksFromFile() throws IOException {        
        String filePath = "classpath:chapters/Blanks.txt";
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
                question.setQuestionType(ExamQuestion.QuestionType.FILL_IN_THE_BLANK);
            } else if (line.startsWith("Ans:")) {
                if (question != null) {
                    question.setCorrectAnswer(line.substring(4).trim());
                }
            }
        }
        if (question != null) {
            examQuestionRepository.save(question);
            blanksQuestions.add(question);
            Collections.shuffle(blanksQuestions);
        }
        reader.close();
        return blanksQuestions;
    }

    
    public List<ExamQuestion> readTrueFalseFromFile() throws IOException {
        String filePath = "classpath:chapters/true-false.txt";
        Resource resource = resourceLoader.getResource(filePath);
        InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<ExamQuestion> trueFalseQuestions = new ArrayList<>();
        Pattern questionPattern = Pattern.compile("^(\\d+)\\.\\s+(.*)$");
        Pattern answerPattern = Pattern.compile("^Ans:\\s+([AB])$");
        String line;
        ExamQuestion question = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            Matcher questionMatcher = questionPattern.matcher(line);
            if (questionMatcher.matches()) {
                if (question != null) {
                    examQuestionRepository.save(question);
                    trueFalseQuestions.add(question);
                }
                question = new ExamQuestion();
                question.setQuestionText(questionMatcher.group(2));
                question.setQuestionType(ExamQuestion.QuestionType.TRUE_FALSE);
                question.setOptionA("True"); // Set option A as "True"
                question.setOptionB("False"); // Set option B as "False"
            } else {
                Matcher answerMatcher = answerPattern.matcher(line);
                if (answerMatcher.matches()) {
                    if (question != null) {
                        question.setCorrectAnswer(answerMatcher.group(1).equals("A") ? "A" : "B");
                    }
                }
            }
        }

        if (question != null) {
            examQuestionRepository.save(question);
            trueFalseQuestions.add(question);
            Collections.shuffle(trueFalseQuestions);
        }

        reader.close();
        return trueFalseQuestions;
    }
    
    public List<ExamQuestionDisplay> transformForDisplay(List<ExamQuestion> questions, Map<Long, String> userAnswers) {
        List<ExamQuestionDisplay> displayQuestions = new ArrayList<>();
        for (ExamQuestion question : questions) {
            ExamQuestionDisplay displayQuestion = new ExamQuestionDisplay();
            displayQuestion.setId(question.getId());
            displayQuestion.setQuestionText(question.getQuestionText());
            displayQuestion.setUserAnswer(userAnswers.get(question.getId()));
            displayQuestion.setCorrectAnswerText(getCorrectAnswerText(question));
            displayQuestions.add(displayQuestion);
        }
        return displayQuestions;
    }
    
    private String getCorrectAnswerText(ExamQuestion question) {
        switch (question.getQuestionType()) {
            case MULTIPLE_CHOICE:
                return getOptionText(question, question.getCorrectAnswer());
            case TRUE_FALSE:
                return question.getCorrectAnswer().equals("A") ? "True" : "False";
            case FILL_IN_THE_BLANK:
                return question.getCorrectAnswer();
            default:
                return "Invalid Answer";
        }
    }
    
    private String getOptionText(ExamQuestion question, String optionLetter) {
        switch (optionLetter) {
            case "A":
                return question.getOptionA();
            case "B":
                return question.getOptionB();
            case "C":
                return question.getOptionC();
            case "D":
                return question.getOptionD();
            default:
                return "Invalid Option";
        }
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
	
	 @Override
	    public List<ExamQuestion> findQuestionsContainingText(String searchText) {
	        return examQuestionRepository.findByQuestionTextContainingIgnoreCase(searchText);
	    }


}
