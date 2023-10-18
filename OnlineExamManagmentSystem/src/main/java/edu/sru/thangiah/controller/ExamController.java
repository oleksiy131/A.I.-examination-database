package edu.sru.thangiah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.sru.thangiah.domain.Exam;
import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.domain.ExamResult;
import edu.sru.thangiah.domain.ExamSubmission;
import edu.sru.thangiah.domain.ExamSubmissionEntity;
import edu.sru.thangiah.domain.Question;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.ExamRepository;
import edu.sru.thangiah.repository.ExamSubmissionRepository;
import edu.sru.thangiah.repository.UserRepository;
import edu.sru.thangiah.service.ExamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *____  __    __        _ _ 
 / __ \/ /__ / /__ ___ (_|_)
/ /_/ / / -_)  '_/(_-</ / / 
\____/_/\__/_/\_\/___/_/_/  
                        
 */

@Controller
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamService examService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ExamSubmissionRepository examSubmissionRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    public ExamController(ExamService examService) {
        this.examService = examService;
    }


    @GetMapping("/{id}")
    public String takeExam(@PathVariable Long id, Model model) {
        // Retrieve the exam by ID from the repository
        Exam exam = examService.getExamById(id);

        if (exam != null) {
            // Check if the exam's duration is still valid
            if (isExamDurationValid(exam)) {
                model.addAttribute("exam", exam);
                return "takeExam"; // Thymeleaf template for taking the exam
            } else {
                model.addAttribute("message", "The exam has expired.");
            }
        } else {
            model.addAttribute("message", "Exam not found.");
        }

        return "error"; // Thymeleaf template for displaying an error message
    }

    @PostMapping("/submit/{id}")
    public String submitExam(@PathVariable Long id, @RequestParam Map<String, String> formParams, Model model) {
        // Retrieve the exam by ID from the repository
        Exam exam = examService.getExamById(id);

        if (exam != null) {
            // Check if the exam's duration is still valid
            if (isExamDurationValid(exam)) {
                // Get the user's answers from the form
                Map<Long, String> userAnswers = new HashMap<>();
                String[] questionIds = formParams.get("questionIds").split(",");
                for (String questionId : questionIds) {
                    String answer = formParams.get("answer_" + questionId);
                    if (answer != null) {
                        userAnswers.put(Long.parseLong(questionId), answer);
                    }
                }

                // Calculate the score based on the submitted answers
                int score = calculateScore(exam, userAnswers);

                // Create an ExamSubmission object with userId, examId, answers, and score
                Long userId = getUserId(); // Implement this method to get the user's ID
                Long examId = id;
                ExamSubmission examSubmission = new ExamSubmission(userId, examId, userAnswers, score);

                // Save the examSubmission (including the score) to the database or another storage system
                saveExamSubmission(examSubmission);

                model.addAttribute("score", score);
                return "showScore"; // Thymeleaf template for displaying the score
            } else {
                model.addAttribute("message", "The exam has expired.");
            }
        } else {
            model.addAttribute("message", "Exam not found.");
        }

        return "error"; // Thymeleaf template for displaying an error message
    }

    // Implement this method to get the user's ID
    private Long getUserId() {
        // Add your logic to retrieve the user's ID
        return 1L; // Replace with actual user ID retrieval logic
    }

    // Implement a method to check if the exam duration is valid
    private boolean isExamDurationValid(Exam exam) {
        // Implement your logic here to check if the exam duration is still valid
        // You can use the exam's start time and durationInMinutes property
        return true; // Modify this based on your logic
    }

 // Implement this method to calculate the score
    private int calculateScore(Exam exam, Map<Long, String> userAnswers) {
        int score = 0;
        List<ExamQuestion> questions = exam.getQuestions();

        // Loop through the questions and compare user answers with correct answers
        for (ExamQuestion question : questions) {
            Long questionId = question.getId();
            String correctAnswer = question.getCorrectAnswer();

            // Retrieve the user's selected answer for this question
            String userAnswer = userAnswers.get(questionId);

            // Check if the user's selected answer matches the correct answer (case-insensitive)
            if (userAnswer != null && userAnswer.equalsIgnoreCase(correctAnswer)) {
                score++; // Increment the score for correct answers
            }
        }

        return score;
    }




    // Placeholder method to save the exam submission to the database or storage
    private void saveExamSubmission(ExamSubmission examSubmission) {
        // Create an ExamSubmissionEntity to store the submission details
        ExamSubmissionEntity submissionEntity = new ExamSubmissionEntity();

        submissionEntity.setUserId(examSubmission.getUserId());
        submissionEntity.setExamId(examSubmission.getExamId());
        submissionEntity.setUserAnswers(examSubmission.getAnswers());
        submissionEntity.setScore(examSubmission.getScore());

        // Save the examSubmission entity
        examSubmissionRepository.save(submissionEntity); 

    }
    


    // This method generates the exam and displays it to the user
    @RequestMapping("/generateExam/{chapter}")
    public String generateExam(Model model, @PathVariable("chapter") int chapter) {
        List<Question> questions = examService.generateExam(chapter, 10);
        model.addAttribute("questions", questions);
        return "exam";
    }
    
    @GetMapping("/pickExam/{chapter}")
    public String pickExam(@PathVariable int chapter, Model model) {
        List<Question> questions = examService.generateExam(chapter, 10); // Or however many you want
        model.addAttribute("questions", questions);
        return "edit-exam"; // This is the name of the Thymeleaf template to be created next
    }
    
    @PostMapping(value = "/submitEditedExam", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> submitEditedExam(@RequestBody List<Question> editedQuestions) {
    	System.out.println("Inside POST");
    	
        byte[] contents = examService.createExcelFile(editedQuestions);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = "edited_exam_questions.xlsx";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return ResponseEntity.ok()
                .headers(headers)
                .body(contents);
    }



    // This method receives the submitted answers, evaluates them, and redirects to the results page
    @PostMapping("/submitAnswers")
    @ResponseBody
    public Map<String, Object> submitAnswers(@RequestBody Map<Integer, String> userAnswers) {
        // Calculate the results based on the user's answers
        ExamResult result = examService.evaluateAnswers(userAnswers);

        // Create a response map to send back as the AJAX response
        Map<String, Object> response = new HashMap<>();
        response.put("redirectUrl", "/submit-answers"); // URL for redirection

        // Store the results in the model to make them available for the results page
        // Note: This assumes that you have a service or session to store and retrieve the results for the next request.
        examService.storeExamResultForUser(result);

        return response;
    }

    // This method prepares and shows the results page to the user
    @GetMapping("/submit-answers")
    public String showResults(Model model) {
        // Retrieve the results from the service or session
        ExamResult result = examService.getStoredExamResultForUser();

        // Add results to the model to make them available for the view
        model.addAttribute("score", result.getScore());
        model.addAttribute("correctAnswers", result.getCorrectAnswers());
        model.addAttribute("incorrectAnswers", result.getIncorrectAnswers());
        model.addAttribute("incorrectAnswersWithCorrections", result.getIncorrectAnswersWithCorrections());


        return "submit-answers"; 
    }
}
