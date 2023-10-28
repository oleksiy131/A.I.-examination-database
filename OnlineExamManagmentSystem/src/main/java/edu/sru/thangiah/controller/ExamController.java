package edu.sru.thangiah.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.sru.thangiah.domain.Exam;
import edu.sru.thangiah.domain.ExamDetails;
import edu.sru.thangiah.domain.ExamQuestion;
import edu.sru.thangiah.domain.ExamResult;
import edu.sru.thangiah.domain.ExamSubmission;
import edu.sru.thangiah.domain.ExamSubmissionEntity;
import edu.sru.thangiah.domain.Question;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.ExamRepository;
import edu.sru.thangiah.repository.ExamSubmissionRepository;
import edu.sru.thangiah.repository.UserRepository;
import edu.sru.thangiah.service.ExamQuestionService;
import edu.sru.thangiah.service.ExamService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
    private ExamQuestionService examQuestionService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ExamSubmissionRepository examSubmissionRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    public ExamController(ExamService examService) {
        this.examService = examService;
    }
    

    @PostMapping("/generate")
    public String generateExam(@ModelAttribute("examDetails") ExamDetails examDetails, Model model) {
        List<Long> selectedExamQuestionIds = examDetails.getSelectedExamQuestionIds();
        
        if (selectedExamQuestionIds == null) {
        	System.out.println("selected question is empty bruh");
        }

     // Fetch selected questions from the database
        List<ExamQuestion> selectedQuestions = selectedExamQuestionIds.stream()
            .map(examQuestionService::getExamQuestionById)
            .collect(Collectors.toList());

        // Create a new exam and set its properties
        Exam exam = new Exam();
        exam.setExamName(examDetails.getExamName());
        exam.setDurationInMinutes(examDetails.getDurationInMinutes());
        exam.setQuestions(selectedQuestions);
        System.out.println("Question set: " + exam.getQuestions());

        
     // Save the exam to the database
        Exam savedExam = examRepository.save(exam);

        // Add the generated exam's ID to the model
        model.addAttribute("generatedExamId", savedExam.getId());

        // Also, add the exam questions to the model again as they were before
        List<ExamQuestion> examQuestions = examQuestionService.getAllExamQuestions();
        model.addAttribute("examQuestions", examQuestions);

        // Return the same view which is used for selecting exam questions, not a new one
        return "generateExam"; // This should be the name of your Thymeleaf template for selecting questions
    }
    


    @GetMapping("/{id}/link")
    public String showExamLink(@PathVariable Long id, Model model) {
        Exam exam = examRepository.findById(id).orElse(null); // Fetching the exam from the database.

        if (exam == null) {
            model.addAttribute("message", "Exam not found");
            return "error-page"; // This should be your error view page.
        }

        // Construct the link that students will use to take the exam. This URL pattern should match your students' exam taking page.
        String examLink = "/exam/take/" + id;

        model.addAttribute("exam", exam); // For displaying other exam details.
        model.addAttribute("examLink", examLink); // The actual link students will use.

        return "display-exam-link"; // This view will display the exam link and details to the faculty.
    }
    
    

    // Method to display chapter selection. This method will need a corresponding HTML file to display options.
    @GetMapping("/selectChapter")
    public String selectChapter(Model model) {
        // Assuming you have a service method that can retrieve all available chapters.
        List<Integer> chapters = examService.getAllChapters();
        model.addAttribute("chapters", chapters);
        return "selectChapter"; // This is a Thymeleaf template that needs to be created.
    }
    
    // Method to handle chapter selection and redirect to exam generation.
    @PostMapping("/selectChapter")
    public String handleChapterSelection(@RequestParam("selectedChapter") int chapter, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("selectedChapter", chapter);
        return "redirect:/exam/generateExam"; // This redirects to the exam generation method.
    }
    
    @GetMapping("/generateExam")
    public String generateExam(@ModelAttribute("selectedChapter") int chapter, Model model) {
        // Generate questions based on the selected chapter.
        List<ExamQuestion> questions = examService.generateQuestionsForChapter(chapter);
        model.addAttribute("questions", questions);
        return "generateExam"; // This is a Thymeleaf template that needs to be created.
    }

    @GetMapping("/{id}")
    public String takeExamz(@PathVariable Long id, Model model) {
        // Retrieve the exam by ID from the repository
        Exam exam = examService.getExamById(id);

        if (exam != null) {
            // Check if the exam's duration is still valid
            if (isExamDurationValid(exam)) {
                model.addAttribute("exam", exam);
                return "takeExam"; 
            } else {
                model.addAttribute("message", "The exam has expired.");
            }
        } else {
            model.addAttribute("message", "Exam not found.");
        }

        return "error"; 
    }
    
    @GetMapping("/take/{id}")
    public String takeExam(@PathVariable Long id, Model model) {
        // Retrieve the exam by ID from the repository
        Exam exam = examService.getExamById(id);
        
        System.out.println("Questions retrieved: " + exam.getQuestions());

        if (exam != null) {
            // Check if the exam's duration is still valid
            //if (isExamDurationValid(exam)) {
                model.addAttribute("exam", exam);
                return "takeExam"; 
          //  } else {
          //      model.addAttribute("message", "The exam has expired.");
          //  }
        } else {
            model.addAttribute("message", "Exam not found.");
        }

        return "error"; // name of your error view template
    }
    
    @GetMapping("/exam/{id}/link")
    public String generateExamLink(@PathVariable Long id, Model model) {
        // Get the exam details from the database using the provided id
        Exam exam = examRepository.findById(id).orElse(null);

        // Check if the exam exists
        if (exam == null) {
            // Handle the case where the exam does not exist
            model.addAttribute("message", "The requested exam does not exist.");
            return "error"; // Name of your error view template
        }

        // Create the exam link
        String examLink = "/exam/take/" + id; // Adjust the link based on your URL structure

        // Add attributes to the model
        model.addAttribute("examLink", examLink);
        model.addAttribute("exam", exam);

        // Return the name of the view template that will display the exam link
        return "examLink"; // You need to create this new Thymeleaf template
    }


    @PostMapping("/submit/{id}")
    public String submitExam(@PathVariable Long id, @RequestParam Map<String, String> formParams, Model model) {
        // Retrieve the exam by ID from the repository
        Exam exam = examService.getExamById(id);

        if (exam != null) {
            // Check if the exam's duration is still valid
            if (isExamDurationValid(exam)) {
                // Parse the form parameters to retrieve question IDs and corresponding user answers
                Map<Long, String> userAnswers = new HashMap<>();
                for (Map.Entry<String, String> entry : formParams.entrySet()) {
                    if (entry.getKey().startsWith("answer_")) {
                        Long questionId = Long.parseLong(entry.getKey().substring(7)); // Extract question ID from the key
                        userAnswers.put(questionId, entry.getValue());
                    }
                }

             // Calculate the total score and identify incorrect questions
                int totalScore = 0;
                List<ExamQuestion> incorrectQuestions = new ArrayList<>();
                for (ExamQuestion question : exam.getQuestions()) {
                    String userAnswer = userAnswers.get(question.getId());
                    question.setUserAnswer(userAnswer); // Recording the user's answer here.
                    if (question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                        totalScore++;
                    } else {
                        incorrectQuestions.add(question);
                    }
                }

                // Obtain the authenticated user's ID
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Long userId = getUserIdFromAuthentication(authentication);

                // Create an ExamSubmission object and populate it with data
                ExamSubmission examSubmission = new ExamSubmission();
                examSubmission.setUserId(userId);
                examSubmission.setExamId(id);
                examSubmission.setAnswers(new ArrayList<>(userAnswers.values()));
                examSubmission.setScore(totalScore);

                // Save the exam submission to the database
                saveExamSubmission(examSubmission, userId);

                // Add attributes to the model for the view
                model.addAttribute("score", totalScore);
                model.addAttribute("totalQuestions", exam.getQuestions().size());
                model.addAttribute("incorrectQuestions", incorrectQuestions);

                return "showScore"; 
            } else {
                model.addAttribute("message", "The exam has expired.");
            }
        } else {
            model.addAttribute("message", "Exam not found.");
        }

        return "error"; 
    }


 // Helper method to obtain the authenticated user's ID
 private Long getUserIdFromAuthentication(Authentication authentication) {
     if (authentication != null && authentication.getPrincipal() instanceof User) {
         User user = (User) authentication.getPrincipal();
         return user.getId();
     }
     return null; // Return null if the user ID cannot be obtained
 }

    // Implement this method to get the user's ID
    private Long getUserId() {
        return 1L;
    }

    private boolean isExamDurationValid(Exam exam) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime examEndTime = exam.getStartTime().plusMinutes(exam.getDurationInMinutes());

        // Debugging lines: print times to the console or examine them in your debugger.
        System.out.println("Current Time: " + now);
        System.out.println("Exam Start Time: " + exam.getStartTime());
        System.out.println("Exam End Time: " + examEndTime);

        return now.isBefore(examEndTime);
    }



 // Implement this method to calculate the total score
    private int calculateTotalScore(Exam exam, Map<Long, String> userAnswers) {
        int totalScore = 0;
        List<ExamQuestion> questions = exam.getQuestions();

        // Loop through the questions and compare user answers with correct answers
        for (ExamQuestion question : questions) {
            Long questionId = question.getId();
            String correctAnswer = question.getCorrectAnswer();

            // Retrieve the user's selected answer for this question
            String userAnswer = userAnswers.get(questionId);

            // Check if the user's selected answer matches the correct answer (case-insensitive)
            if (userAnswer != null && userAnswer.equalsIgnoreCase(correctAnswer)) {
                totalScore++; // Increment the total score for correct answers
            }
        }

        return totalScore;
    }





 // Placeholder method to save the exam submission to the database or storage
    private void saveExamSubmission(ExamSubmission examSubmission, Long userId) {
        // Create an ExamSubmissionEntity to store the submission details
        ExamSubmissionEntity submissionEntity = new ExamSubmissionEntity();
        
        // Assuming you have an examRepository and a userRepository, retrieve the Exam and User objects
        Exam exam = examRepository.findById(examSubmission.getExamId()).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (exam != null && user != null) {
            // Set the relevant properties of the entity based on the examSubmission object
            submissionEntity.setUser(user);
            submissionEntity.setExam(exam);
            submissionEntity.setUserAnswers(examSubmission.getAnswers());
            submissionEntity.setScore(examSubmission.getScore());

            // Save the exam submission entity to the database
            examSubmissionRepository.save(submissionEntity);

        } else {

        }
    }
    
    @PostMapping("/exam/generate")
    public String generateExam(@ModelAttribute("examDetails") ExamDetails examDetails, RedirectAttributes redirectAttributes) {
        // Assuming examDetails now includes a field for 'chapter' which is populated from the form.
        int chapter = examDetails.getChapter();

        // Fetch selected questions based on the chapter.
        List<ExamQuestion> selectedQuestions = examService.generateQuestionsForChapter(chapter);

        // Create a new exam and set its properties.
        Exam exam = new Exam();
        exam.setExamName(examDetails.getExamName());
        exam.setDurationInMinutes(examDetails.getDurationInMinutes());
        exam.setQuestions(selectedQuestions);

        // Save the exam to the database.
        Exam savedExam = examRepository.save(exam);

        // Add the generated exam's ID as a flash attribute. This makes it available after the redirect.
        redirectAttributes.addFlashAttribute("generatedExamId", savedExam.getId());

        // Redirect to the GET request handler which displays the form.
        return "redirect:/exam/questions"; // Or to the appropriate URL after exam generation.
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
        return "edit-exam"; 
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
