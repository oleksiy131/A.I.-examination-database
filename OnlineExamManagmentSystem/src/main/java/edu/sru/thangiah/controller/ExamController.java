package edu.sru.thangiah.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import jakarta.servlet.http.HttpSession;

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
    
    
    @PostMapping("/manual-auto-generate")
    public ResponseEntity<String> generateExam(
            @RequestParam("chapter") int chapter, 
            @RequestParam("numMultipleChoice") int numMultipleChoice,
            @RequestParam("numTrueFalse") int numTrueFalse,
            @RequestParam("numBlanks") int numBlanks,
            HttpSession session) throws IOException {
        
        Long examId = (Long) session.getAttribute("currentExamId");
        if (examId == null) {
            return ResponseEntity.badRequest().body("No exam initiated for question generation.");
        }

        Optional<Exam> existingExam = examRepository.findById(examId);
        if (!existingExam.isPresent()) {
            return ResponseEntity.badRequest().body("Exam does not exist.");
        }
        Exam exam = existingExam.get();

        List<ExamQuestion> blanksQuestions = examQuestionService.generateFillInTheBlanksQuestions(numBlanks);
        List<ExamQuestion> trueFalseQuestions = examQuestionService.readTrueFalseFromFile();
        trueFalseQuestions = trueFalseQuestions.subList(0, Math.min(numTrueFalse, trueFalseQuestions.size()));
        List<ExamQuestion> allQuestionsForChapter = examQuestionService.generateQuestionsForChapter(chapter);
        List<ExamQuestion> multipleChoiceQuestions = allQuestionsForChapter.stream()
                .filter(q -> q.getOptionA() != null && q.getOptionB() != null && q.getOptionC() != null && q.getOptionD() != null)
                .limit(numMultipleChoice)
                .collect(Collectors.toList());

        List<ExamQuestion> combinedQuestions = new ArrayList<>();
        combinedQuestions.addAll(blanksQuestions);
        combinedQuestions.addAll(trueFalseQuestions);
        combinedQuestions.addAll(multipleChoiceQuestions);
        exam.setQuestions(combinedQuestions);

        examRepository.save(exam);

        return ResponseEntity.ok().body(String.valueOf(exam.getId()));
    }
    
    @GetMapping("/confirmation/{examId}")
    public String confirmExam(@PathVariable Long examId, Model model) {
        Optional<Exam> exam = examRepository.findById(examId);
        if (!exam.isPresent()) {
            // Handle the case where the exam does not exist
            return "error"; 
        }
        
        model.addAttribute("generatedExamId", examId);
        model.addAttribute("examDetails", exam.get());
        model.addAttribute("selectedQuestions", exam.get().getQuestions());
        return "autoExamConfirmation"; 
    }

    
    
    @PostMapping("/generate")
    public String generateExam(@ModelAttribute("examDetails") ExamDetails examDetails, 
                               Model model, 
                               HttpSession session) {
        // Fetch the exam ID from the session
        Long examId = (Long) session.getAttribute("currentExamId");
        if (examId == null) {
            // Handle error: No exam ID available
            return "error"; // Redirect to an error page or handle accordingly
        }

        List<Long> selectedExamQuestionIds = examDetails.getSelectedExamQuestionIds();
        if (selectedExamQuestionIds == null || selectedExamQuestionIds.isEmpty()) {
            System.out.println("Selected questions are empty bruh");
            // Handle the case where no questions were selected
            return "redirect:/path-to-question-selection"; 
        }

        // Fetch the existing exam instead of creating a new one
        Optional<Exam> optionalExam = examRepository.findById(examId);
        if (!optionalExam.isPresent()) {
            // Handle the case where the exam does not exist
            return "error"; // Redirect to an error page or handle accordingly
        }
        Exam exam = optionalExam.get();

        // Fetch selected questions from the database and add them to the exam
        List<ExamQuestion> selectedQuestions = selectedExamQuestionIds.stream()
                .map(examQuestionService::getExamQuestionById)
                .collect(Collectors.toList());
        exam.setQuestions(selectedQuestions);

        // Save the updated exam to the database
        examRepository.save(exam);

        // Add the exam ID to the model for the confirmation page
        model.addAttribute("generatedExamId", exam.getId());

        // Add the list of selected questions to the model
        model.addAttribute("selectedQuestions", selectedQuestions); // This line ensures the selected questions are passed to the view

        // Return the view that confirms the exam generation
        return "examGeneratedConfirmation"; // Redirect to a confirmation page
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
        try {
            Exam exam = examService.getExamById(id);
            if (exam != null) {
                List<ExamQuestion> questions = exam.getQuestions();
                if (questions != null && !questions.isEmpty()) {
                    model.addAttribute("exam", exam);
                    // Log to check if the questions are retrieved successfully
                    System.out.println("Exam with ID " + id + " has the following questions: " + questions);
                    return "takeExam"; // The name of the Thymeleaf template
                } else {
                    // Log for debugging: No questions found for the exam
                    System.out.println("No questions found for the exam with ID " + id);
                    model.addAttribute("message", "No questions available for this exam.");
                }
            } else {
                // Log for debugging: Exam not found
                System.out.println("Exam not found with ID " + id);
                model.addAttribute("message", "Exam not found.");
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes
            System.out.println("An error occurred while trying to take the exam with ID " + id + ": " + e.getMessage());
            model.addAttribute("message", "An error occurred while retrieving the exam details.");
        }
        return "error"; // The name of the error view template
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
                    // Assuming the formParams keys are in the format "answers[questionId]"
                    String key = entry.getKey();
                    if (key.startsWith("answers[")) {
                        String questionIdStr = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
                        Long questionId = Long.parseLong(questionIdStr);
                        userAnswers.put(questionId, entry.getValue());
                    }
                }

                // Calculate the total score and identify incorrect questions
                int totalScore = 0;
                List<ExamQuestion> incorrectQuestions = new ArrayList<>();
                for (ExamQuestion question : exam.getQuestions()) {
                    String userAnswer = userAnswers.get(question.getId());
                    if (question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                        totalScore++;
                    } else {
                        // If the answer is incorrect, set the user's answer for review and add to the list
                        question.setUserAnswer(userAnswer);
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

        return "error"; // This can be a generic error page or specific to this context
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
