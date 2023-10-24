package edu.sru.thangiah.service;

import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.repository.ExamQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            }
        }
        reader.close();
    }
    
    public List<Integer> getAllChapters() {
        // This method should interact with the repository to fetch all distinct chapters available.
        // Assuming you have a method in your repository class that fetches all unique chapter numbers.
        return examQuestionRepository.findAllDistinctChapters();
    }
    
    public List<ExamQuestion> generateQuestionsForChapter(int chapter) {
        // This method fetches questions from a specific chapter.
        // Assumes you have a method in your repository to find questions by chapter.
        return examQuestionRepository.findQuestionsByChapter(chapter);
    }


	 @Override
	    public List<ExamQuestion> getQuestionsByChapter(int chapter) {
	        return examQuestionRepository.findByChapter(chapter);
	    }

	@Override
	public List<ExamQuestion> getAllExamQuestions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExamQuestion getExamQuestionById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveExamQuestion(ExamQuestion examQuestion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteExamQuestion(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readExamQuestionsFromFile() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
