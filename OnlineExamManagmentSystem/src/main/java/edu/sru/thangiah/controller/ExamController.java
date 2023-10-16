package edu.sru.thangiah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.sru.thangiah.domain.ExamResult;
import edu.sru.thangiah.domain.Question;
import edu.sru.thangiah.service.ExamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExamController {

    @Autowired
    private ExamService examService;
    
    public ExamController(ExamService examService) {
        this.examService = examService;
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
