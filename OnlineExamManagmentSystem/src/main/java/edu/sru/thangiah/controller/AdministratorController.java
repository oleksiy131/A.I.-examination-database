package edu.sru.thangiah.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.thangiah.domain.Administrator;
import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.ScheduleManager;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.AdministratorRepository;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.ScheduleManagerRepository;
import edu.sru.thangiah.repository.StudentRepository;
import edu.sru.thangiah.repository.UserRepository;
import edu.sru.thangiah.service.EmailService;
import edu.sru.thangiah.web.dto.ChatGPTRequest;
import edu.sru.thangiah.web.dto.ChatGptResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class AdministratorController {

    @Value("${spring.datasource.username}")
    private String adminUsername;

    @Value("${spring.datasource.password}")
    private String adminPassword;

    @Autowired
    private AdministratorRepository administratorRepository;
    @Autowired
	private StudentRepository studentRepository;
    @Autowired
	private CourseRepository courseRepository;
    @Autowired
    private  InstructorRepository instructorRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  ScheduleManagerRepository scheduleManagerRepository;
    


    @GetMapping("/administratorlogin")
    public String showLoginPage() {
        return "administratorlogin"; // Return the administratorlogin.html template
    }

    @PostMapping("/index")
    public String adminLogin(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        RedirectAttributes redirectAttributes
    ) {
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            // Successful login, redirect to the navbar screen
            return "redirect:/navbar";
        } else {
            // Invalid credentials, display an error message
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid credentials");
            return "redirect:/index";
        }
    }

    // This method handles HTTP POST requests to create a new Administrator.
    @PostMapping("/process_login")
    public Administrator createAdministrator(@RequestBody Administrator administrator) {
        // Save the Administrator object to the repository and return the saved user.
        return administratorRepository.save(administrator);
    }
    
    @GetMapping("/exams")
    public String examsPage() {
        // display the list of exams here
        return "exams";
    }
    
    @GetMapping("/classes")
    public String classesPage() {
        // displays the list of classes here
        return "classes"; 
    }
    
    @GetMapping("/maths-quiz")
    public String mathQuizPage() {
        // displays the math quiz
        return "maths-quiz"; 
    }
    
    @GetMapping("/history-quiz")
    public String historyQuizPage() {
        // displays the math quiz
        return "history-quiz"; 
    }
    
    @GetMapping("/science-quiz")
    public String scienceQuizPage() {
        // displays the science quiz
        return "science-quiz"; 
    }
    
    @GetMapping("/create-student")
    public String showCreateStudentForm() {
        return "create-student"; 
    }
    
    @GetMapping("/create-instructor")
    public String showCreateInstructorForm() {
        return "create-instructor"; 
    }
    
    
    
    @GetMapping("/import")
    public String importStudents() {
        return "import";
    }
    
    @GetMapping("/associate")
    public String associateStudentWithCourseForm(Model model) {
        // Retrieve the list of students and courses from the repository
        List<Student> students = studentRepository.findAll();
        List<Course> courses = courseRepository.findAll();

        // Add the lists of students and courses to the model for rendering in the HTML template
        model.addAttribute("students", students);
        model.addAttribute("courses", courses);

        // Return the name of the HTML template for the form
        return "associate-students";
    }
    
    @GetMapping("/associate-instructor")
    public String associateInstructorWithCourseForm(Model model) {
        // Retrieve the list of instructors and courses from the repository
        List<Instructor> instructors = instructorRepository.findAll();
        List<Course> courses = courseRepository.findAll();

        // Add the lists of instructors and courses to the model for rendering in the HTML template
        model.addAttribute("instructors", instructors);
        model.addAttribute("courses", courses);

        // Return the name of the HTML template for the form
        return "associate-instructor";
    }
    

	

    
    @GetMapping("/upload-success")
    public String uploadSuccess() {
        return "upload-success"; 
    }
    
    
    @GetMapping("/students")
    public String showStudentList(Model model) {
        // Retrieve the list of students from the repository
        List<Student> students = (List<Student>) studentRepository.findAll();

        // Add the list of students to the model for rendering in the HTML template
        model.addAttribute("students", students);

        // Return the name of the HTML template to be displayed
        return "student-list";
    }
    
    
	 @PostMapping("/student/course/associate")
	 public ResponseEntity<String> associateStudentWithCourse(
	      @RequestParam Long studentId,
	      @RequestParam Long courseId,
	      Model model) {
	      // Retrieve the student and course entities from the repository
	      Student student = studentRepository.findById(studentId).orElse(null);
	      Course course = courseRepository.findById(courseId).orElse(null);

	      // Check if both entities exist
	      if (student != null && course != null) {
	          // Add the course to the student's courses
	          student.getCourses().add(course);
	          studentRepository.save(student);
	          return ResponseEntity.ok("Student associated with the course successfully");
	      } else {
	          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Student or course not found");
	      }
	 }
	 
	 @GetMapping("/register")
	    public String showRegistrationForm() {
	        return "register";
	    }

	    @PostMapping("/register")
	    public String registerUser(
	            @RequestParam String firstName,
	            @RequestParam String lastName,
	            @RequestParam String email,
	            @RequestParam String password,
	            @RequestParam String username,
	            @RequestParam String role) {
	        
	        // Create a new user with the provided information
	        User user = new User(firstName, lastName, email, password, username, role);
	        
	        // Save the user to the database using JpaRepository's save method
	        userRepository.save(user);
	        
	        // Send a verification email
	        sendVerificationEmail(user);

	        // Redirect to a confirmation page or login page
	        return "redirect:/registration-confirmation"; 
	    }
	    
	    @GetMapping("/registration-confirmation")
	    public String registerConfirm() {
	        return "registration-confirmation"; 
	    }
	    
	 // Send verification email to the user
	    private void sendVerificationEmail(User user) {
	        String subject = "Email Verification";
	        String message = "Your verification code is: " + user.getVerificationCode();
	        String recipientEmail = user.getEmail();

	        try {
	            emailService.sendEmail(recipientEmail, subject, message);
	        } catch (Exception e) {
	            // Handle the exception if any
	        }
	    }
	
	    
	    @GetMapping("/chat-page")
	    public String chatPage() {
	        return "chat";
	    }
	    
	    @GetMapping("/schedule-managers")
	    public String showScheduleManagerList(Model model) {
	        // Retrieve the list of schedule managers from the repository
	        List<ScheduleManager> managers = (List<ScheduleManager>) scheduleManagerRepository.findAll();

	        // Add the list of schedule managers to the model for rendering in the HTML template
	        model.addAttribute("managers", managers);

	        // Return the name of the HTML template to be displayed
	        return "schedule-manager-list";  // Assuming the HTML file is named 'schedule-manager-list.html'
	    }
	    
	    
	    
	   
	 


}

