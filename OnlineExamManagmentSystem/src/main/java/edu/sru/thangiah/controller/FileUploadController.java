package edu.sru.thangiah.controller;

import edu.sru.thangiah.domain.Exam;
import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.repository.ExamRepository;
import edu.sru.thangiah.service.ExamQuestionService;
import edu.sru.thangiah.service.ExamService; // Import the ExamService if it exists
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
 *____  __    __        _ _ 
 / __ \/ /__ / /__ ___ (_|_)
/ /_/ / / -_)  '_/(_-</ / / 
\____/_/\__/_/\_\/___/_/_/  
                        
 */

@Controller
@RequestMapping("/file")
public class FileUploadController {
	
	@Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamQuestionService examQuestionService;

    @Autowired
    private ExamService examService; 

    @PostMapping("/upload-questions")
    public String uploadQuestionsAndGenerateExam(@RequestParam("file") MultipartFile file, 
                                                RedirectAttributes redirectAttributes, 
                                                Model model) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "No file uploaded");
            return "redirect:/upload-path"; // Replace with your actual upload page path
        }
        try {
            // Read questions from file and get the list of persisted questions
            List<ExamQuestion> uploadedQuestions = examQuestionService.readAIQuestionsFromFile(file);

            // Generate the exam with ID 99, name 'AI Exam', and duration 15 minutes
            Exam exam = new Exam();
            exam.setExamName("Exam"); // Set the exam name
            exam.setDurationInMinutes(15); // Set the exam duration
            exam.setQuestions(uploadedQuestions); // Set only the uploaded questions to the exam

            // Save the exam to the database using the ExamRepository
            examRepository.save(exam);

            // Add attributes to the model for the confirmation page
            model.addAttribute("generatedExamId", exam.getId());
            model.addAttribute("examDuration", exam.getDurationInMinutes());
            model.addAttribute("selectedQuestions", uploadedQuestions);

            // Redirect to the exam generation confirmation page
            return "examGeneratedConfirmation"; // Return the view name of the confirmation page
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file and generate exam: " + e.getMessage());
            return "redirect:/upload-path"; // Replace with your actual upload page path
        }
        
    }
    
    @PostMapping("/upload/new/exam")
    public String updateExamWithQuestions(@RequestParam("file") MultipartFile file,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes,
                                          Model model) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "No file uploaded");
            return "redirect:/upload-path"; // Replace with your actual upload page path
        }

        // Fetch the exam ID from the session
        Long examId = (Long) session.getAttribute("currentExamId");
        if (examId == null) {
            redirectAttributes.addFlashAttribute("message", "No exam ID available");
            return "redirect:/error"; // Redirect to an error page or handle accordingly
        }

        try {
            // Fetch the existing exam
            Optional<Exam> optionalExam = examRepository.findById(examId);
            if (!optionalExam.isPresent()) {
                redirectAttributes.addFlashAttribute("message", "Exam not found");
                return "redirect:/error"; // Redirect to an error page or handle accordingly
            }
            Exam exam = optionalExam.get();

            // Read questions from file and get the list of questions
            List<ExamQuestion> uploadedQuestions = examQuestionService.readAIQuestionsFromFile(file);
            if (uploadedQuestions.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "No questions found in the file");
                return "redirect:/upload"; // Redirect to the upload page or handle accordingly
            }

            // Update the existing exam with new questions
            exam.setQuestions(uploadedQuestions);
            examRepository.save(exam);

            // Add attributes to the model for the confirmation page
            model.addAttribute("generatedExamId", exam.getId());
            model.addAttribute("examDuration", exam.getDurationInMinutes());
            model.addAttribute("selectedQuestions", uploadedQuestions);

            // Redirect to the exam update confirmation page
            return "examGeneratedConfirmation"; 
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file and update exam: " + e.getMessage());
            return "redirect:/upload-path"; // Replace with your actual upload page path
        }
    }

}
