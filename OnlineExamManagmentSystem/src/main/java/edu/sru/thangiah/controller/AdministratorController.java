package edu.sru.thangiah.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.thangiah.domain.Administrator;
import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.ScheduleManager;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.AdministratorRepository;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.RoleRepository;
import edu.sru.thangiah.repository.ScheduleManagerRepository;
import edu.sru.thangiah.repository.StudentRepository;
import edu.sru.thangiah.repository.UserRepository;
import edu.sru.thangiah.service.EmailService;

import java.util.List;
import java.util.Optional;

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
    private RoleRepository roleRepository;
    @Autowired
    private ScheduleManagerRepository managerRepo;
    
    
    
    @RequestMapping("/navbar")
    public String showMainScreen() {
        return "navbar"; 
    }


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
        return "exams"; // the name of the HTML template for the exams page
    }
    
    @GetMapping("/classes")
    public String classesPage() {
        // displays the list of classes here
        return "classes"; // the name of the HTML template for the classes page
    }
    
    @GetMapping("/math-quiz")
    public String mathQuizPage() {
        // displays the math quiz
        return "math-quiz"; // the name of the HTML template for the quiz page
    }
    
    @GetMapping("/history-quiz")
    public String historyQuizPage() {
        // displays the math quiz
        return "history-quiz"; // the name of the HTML template for the quiz page
    }
    
    @GetMapping("/science-quiz")
    public String scienceQuizPage() {
        // displays the science quiz
        return "science-quiz"; // the name of the HTML template for the quiz page
    }
    
    @GetMapping("/create-student")
    public String showCreateStudentForm() {
        return "create-student"; // This corresponds to the name of your HTML file
    }
    
    @GetMapping("/create-instructor")
    public String showCreateInstructorForm() {
        return "create-instructor"; // This corresponds to the name of your HTML file
    }
    
    @GetMapping("/add-course")
    public String showCreateCourseForm() {
        return "add-course"; // This corresponds to the name of your HTML file
    }
    
    @GetMapping("/import")
    public String importStudents() {
        return "import"; // This corresponds to the name of your HTML file
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
    

	// Endpoint to associate an instructor with a course
	    @PostMapping("/instructor/course/associate")
	    public ResponseEntity<String> associateInstructorWithCourse(
	        @RequestParam Long instructorId,
	        @RequestParam Long courseId,
	        Model model) {

	        // Retrieve the instructor and course entities from the repository
	        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
	        Course course = courseRepository.findById(courseId).orElse(null);

	        // Check if both entities exist
	        if (instructor != null && course != null) {
	            // Add the course to the instructor's courses
	            instructor.getCourses().add(course);
	            instructorRepository.save(instructor);
	            return ResponseEntity.ok("Instructor associated with the course successfully");
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Instructor or course not found");
	        }
	    }


    
    @GetMapping("/upload-success")
    public String uploadSuccess() {
        return "upload-success"; // This corresponds to the name of your HTML file
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
	        return "register"; // This maps to the register.html file
	    }

	    @PostMapping("/register")
	    public String registerUser(
	            @RequestParam String firstName,
	            @RequestParam String lastName,
	            @RequestParam String email,
	            @RequestParam String password,
	            @RequestParam String username,
	            @RequestParam String role) {
	        
	    	switch (role) {
	        case "ADMINISTRATOR":
	        	Optional<Roles> adminRole = roleRepository.findByName(role);
		    	
		    	Administrator adminUser = new Administrator();
		    	
		    	adminUser.setAdminFirstName(firstName);
		    	adminUser.setAdminLastName(lastName);
		    	adminUser.setAdminEmail(email);
		    	adminUser.setAdminUsername(username);
		    	adminUser.setAdminPassword(password);
		    	//adminUser.setRole(adminRole);
		    	
		    	if (!userRepository.findByUsername(adminUser.getAdminUsername()).isPresent()) {
		    		administratorRepository.save(adminUser);
	            }

	            break;
	        case "SCHEDULE_MANAGER":
	        	Optional<Roles> managerRole = roleRepository.findByName(role);
		    	
		    	ScheduleManager managerUser = new ScheduleManager();
		    	
		    	managerUser.setManagerFirstName(firstName);
		    	managerUser.setManagerLastName(lastName);
		    	managerUser.setManagerEmail(email);
		    	managerUser.setManagerUsername(username);
		    	managerUser.setManagerPassword(password);
		    	//managerUser.setRole(managerRole);
	        	
		    	if (!userRepository.findByUsername(managerUser.getManagerUsername()).isPresent()) {
		    		managerRepo.save(managerUser);
	            }

	            break;
	        case "STUDENT":
	        	Optional<Roles> studentRole = roleRepository.findByName(role);
		    	
		    	Student studentUser = new Student();
		    	
		    	studentUser.setStudentFirstName(firstName);
		    	studentUser.setStudentLastName(lastName);
		    	studentUser.setStudentEmail(email);
		    	studentUser.setStudentUsername(username);
		    	studentUser.setStudentPassword(password);
		    	//studentUser.setRole(studentRole);
	        	
		    	if (!userRepository.findByUsername(studentUser.getStudentUsername()).isPresent()) {
	                studentRepository.save(studentUser);
	            }
		    	
	            break;
	        case "INSTRUCTOR":
	        	Optional<Roles> instructorRole = roleRepository.findByName(role);
		    	
	        	Instructor instructorUser = new Instructor();
		    	
	        	instructorUser.setInstructorFirstName(firstName);
	        	instructorUser.setInstructorLastName(lastName);
	        	instructorUser.setInstructorEmail(email);
	        	instructorUser.setInstructorUsername(username);
	        	instructorUser.setInstructorPassword(password);
		    	//instructorUser.setRole(instructorRole);
	        	if (!userRepository.findByUsername(instructorUser.getInstructorUsername()).isPresent()) {
	                instructorRepository.save(instructorUser);
	            }
		    	
	            break;
	        default:
	            System.out.println("Unknown role: " + role);
	    }
			/*
			 * // Create a new user with the provided information User user = new
			 * User(firstName, lastName, email, password, username, role);
			 * 
			 * // Save the user to the database using JpaRepository's save method
			 * userRepository.save(user);
			 * 
			 * // Send a verification email sendVerificationEmail(user);
			 */
	        // Redirect to a confirmation page or login page
	        return "redirect:/registration-confirmation"; // 
	    }
	    
	    @GetMapping("/registration-confirmation")
	    public String registerConfirm() {
	        return "registration-confirmation"; // The HTML file
	    }
	    
	 // Send verification email to the user
	    private void sendVerificationEmail(User user) {
	        String subject = "Email Verification";
	        String message = "Your verification code is: " + user.getVerificationCode();
	        String recipientEmail = user.getEmail();

	        try {
	            emailService.sendEmail(recipientEmail, subject, message);
	        } catch (Exception e) {
	            // Handle the exception (e.g., log it)
	        }
	    }
	    
	    
	    @GetMapping("/lister")
	    public String userLister(Model model) {
	   
	    	 List<User> users = (List<User>) userRepository.findAll();

	        // Add the list of user to the model for rendering in the HTML template
	        model.addAttribute("Users", users);
			return "lister";
	    }
	    
	    
	    @GetMapping("/rooty")
	    public String rootHome(Model model) {
			List<User> listUsers = userRepository.findAll();
		    model.addAttribute("listUsers", listUsers);
	        return "rootHome"; // The HTML file
	    }
	 


}

