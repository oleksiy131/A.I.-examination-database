package edu.sru.thangiah.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

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
	private InstructorRepository instructorRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private ScheduleManagerRepository SMRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;

	// This method handles HTTP POST requests to create a new Administrator.
	@PostMapping("/process_login")
	public Administrator createAdministrator(@RequestBody Administrator administrator) {
		// Save the Administrator object to the repository and return the saved user.
		return administratorRepository.save(administrator);
	}
	
	@GetMapping("/admin_homepage")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public String viewAdminHomepage() {
		return "admin_homepage";
		
	}
	
	
	@GetMapping("/av-account-management")
	public String accountManager(Model model){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		System.out.println(auth.getName());
		System.out.println(auth.getPrincipal());
		System.out.println(auth.getDetails());
		
		String instructorUser = auth.getName();
		
		List<Administrator> administrator = administratorRepository.findByAdminUsernameContaining(instructorUser);
		
		model.addAttribute("administrator", administrator);
					
		return "av-account-management";
	}
	
	
	@GetMapping("/av-edit-current-admin/{id}")
	public String editingCurrentUser(@PathVariable("id") long id, Model model) {
		Administrator administrator = administratorRepository.findById(id)
    		      .orElseThrow(() -> new IllegalArgumentException("Invalid instructor Id:" + id));
    		    
    		    model.addAttribute("administrator", administrator);
	    return "av-edit-current-admin"; 
	}

	  @Transactional
			@PostMapping("/av-edit-admin/{id}")
			public String saveCurrentUserEdits(@PathVariable("id") long id, @Validated Administrator administrator , 
			  BindingResult result, Model model,@RequestParam(value = "currentPassword", required = false) String currentPassword, @RequestParam(value = "newPassword", required = false) String newAdminPassword, 
			  @RequestParam(value = "confirmPassword", required = false) String confirmAdminPassword) {
			    if (result.hasErrors()) {
			    	administrator.setAdminId(id); 
			        return "av-edit-current-admin";
			    }
			    
			    
			    User user = userRepository.findByUsername(administrator.getAdminUsername())
			            .orElse(new User());  

			    boolean passwordError = false;

			    // Only process password if new password fields are filled
			    if (newAdminPassword != null && !newAdminPassword.isEmpty()) {
			        // Verify current password if provided
			        if (currentPassword != null && !passwordEncoder.matches(currentPassword, user.getPassword())) {
			            model.addAttribute("passwordError", "Current password is incorrect");
			            passwordError = true;
			        } 
			        // Validate the new password and confirm password
			        else if (!newAdminPassword.equals(confirmAdminPassword)) {
			            model.addAttribute("passwordError", "Passwords do not match");
			            passwordError = true;
			        } 
			        // Encrypt and set the new password if there's no error
			        else {
			            String encryptedPassword = passwordEncoder.encode(newAdminPassword);
			            administrator.setAdminPassword(encryptedPassword);
			            user.setPassword(encryptedPassword); // Update the user's password
			        }
			    }

			    // If there was a password error, re-display the form
			    if (passwordError) {
			        model.addAttribute("administrator", administrator);
			        return "av-edit-current-admin";
			    }

			    // Update other user properties
			    user.setUsername(administrator.getAdminUsername());
			    user.setEmail(administrator.getAdminEmail());
			    userRepository.save(user);  // Save the user to userRepository

			    // Save the administrator
			    administratorRepository.save(administrator);
			    // Debugging: Print the received instructor data
			    System.out.println("Received Instructor Data:");
			    System.out.println("ID: " + administrator.getAdminId());
			    System.out.println("First Name: " + administrator.getAdminFirstName());
			    System.out.println("Last Name: " + administrator.getAdminFirstName());
			    System.out.println("Email: " + administrator.getAdminEmail());
			    System.out.println("Path Variable ID: " + id);
			    
			    return "av-admin-edit-confirmation"; 
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

		// Add the lists of students and courses to the model for rendering in the HTML
		// template
		model.addAttribute("students", students);
		model.addAttribute("courses", courses);

		// Return the name of the HTML template for the form
		return "associate-students";
	}

//	@GetMapping("/associate-instructor")
//	public String associateInstructorWithCourseForm(Model model) {
//		// Retrieve the list of instructors and courses from the repository
//		List<Instructor> instructors = instructorRepository.findAll();
//		List<Course> courses = courseRepository.findAll();
//
//		// Add the lists of instructors and courses to the model for rendering in the
//		// HTML template
//		model.addAttribute("instructors", instructors);
//		model.addAttribute("courses", courses);
//
//		// Return the name of the HTML template for the form
//		return "associate-instructor";
//	}

	// Endpoint to associate an instructor with a course
	@PostMapping("/instructor/course/associate")
	public ResponseEntity<String> associateInstructorWithCourse(@RequestParam Long instructorId,
			@RequestParam Long courseId, Model model) {

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
	
	@GetMapping("/upload-fail")
	public String uploadFail() {
		return "upload-fail"; // This corresponds to the name of your HTML file
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
	
	@GetMapping("/students_list")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public String showStudentsListAV(Model model) {
		// Retrieve the list of students from the repository
		List<Student> students = (List<Student>) studentRepository.findAll();

		// Add the list of students to the model for rendering in the HTML template
		model.addAttribute("students", students);

		// Return the name of the HTML template to be displayed
		return "av-student-list";
	}
	

	@PostMapping("/student/course/associate")
	public ResponseEntity<String> associateStudentWithCourse(@RequestParam Long studentId, @RequestParam Long courseId,
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
	 public String showRegistrationForm(Model model) {
	 model.addAttribute("user", new User()); 
	 return "register"; // This maps to the register.html file 
	  }
	 
	 @GetMapping("/av-register") 
	 @PreAuthorize("hasRole('ADMINISTRATOR')")
	 public String showRegistrationFormAV(Model model) {
	 model.addAttribute("user", new User()); 
	 return "av-register"; // This maps to the register.html file 
	  }
	 

	 
		@Transactional
		@PostMapping("/register-av")
		@PreAuthorize("hasRole('ADMINISTRATOR')")
		public String registerUserAV(@ModelAttribute ScheduleManager manager, RedirectAttributes redirectAttributes) {

			if (SMRepo.findBymanagerUsername(manager.getManagerUsername()).isPresent()) {
				redirectAttributes.addFlashAttribute("errorMessage", "Manager with given username already exists.");
				return "redirect:/register";
			}

			Roles roles = roleRepository.findById(4L).orElseThrow(() -> new RuntimeException("Role with ID 4 not found"));
			List<Roles> rolesList = new ArrayList<>();
			rolesList.add(roles);
			manager.setRoles(rolesList);

			SMRepo.save(manager);

			User user = new User();
			user.setEmail(manager.getManagerEmail());
			user.setFirstName(manager.getManagerFirstName());
			user.setLastName(manager.getManagerLastName());
			user.setUsername(manager.getManagerUsername());
			String hashedPassword = passwordEncoder.encode(manager.getManagerPassword());
			user.setPassword(hashedPassword);


			
			rolesList.add(roles);
			user.setRoles(rolesList);

			user.setEnabled(true);
			userRepository.save(user);

	        
	        
//	        // Send a verification email
	        //sendVerificationEmail(user);
	//
//	        // Redirect to a confirmation page or login page
			return "redirect:/av-registration-confirmation"; //
		}
		
	@Transactional
	@PostMapping("/register")
	public String registerUser(@ModelAttribute ScheduleManager manager, RedirectAttributes redirectAttributes) {

		if (SMRepo.findBymanagerUsername(manager.getManagerUsername()).isPresent()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Manager with given username already exists.");
			return "redirect:/register";
		}

		Roles roles = roleRepository.findById(4L).orElseThrow(() -> new RuntimeException("Role with ID 4 not found"));
		List<Roles> rolesList = new ArrayList<>();
		rolesList.add(roles);
		manager.setRoles(rolesList);

		SMRepo.save(manager);

		User user = new User();
		user.setEmail(manager.getManagerEmail());
		user.setFirstName(manager.getManagerFirstName());
		user.setLastName(manager.getManagerLastName());
		user.setUsername(manager.getManagerUsername());
		String hashedPassword = passwordEncoder.encode(manager.getManagerPassword());
		user.setPassword(hashedPassword);


		
		rolesList.add(roles);
		user.setRoles(rolesList);

		user.setEnabled(true);
		userRepository.save(user);

        
        
//        // Send a verification email
        //sendVerificationEmail(user);
//
//        // Redirect to a confirmation page or login page
		return "redirect:/registration-confirmation"; //
	}
	
	@GetMapping("/av-edit-student/{id}")
    public String showUpdateFormAV(@PathVariable("id") long id, Model model) {
		Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        model.addAttribute("student", student);
        return "av-edit-student";
    }
	
	@Transactional
	@PostMapping("/av-update/{id}")
	public String updateStudentAV(@PathVariable("id") long id, @Validated Student student, BindingResult result,
			Model model, @RequestParam("newPassword") String newStudentPassword,
			@RequestParam("confirmPassword") String confirmStudentPassword) {
		if (result.hasErrors()) {
			student.setStudentId(id);
			return "av-edit-student";
		}
		
		Student Updatestudent = studentRepository.findByStudentUsername(student.getStudentUsername()).orElse(null);

		Set<Course> studentCourses = Updatestudent.getCourses();

		System.out.println(studentCourses);

		// checking the user to exist and creating it if it does not already exist
		User user = userRepository.findByUsername(Updatestudent.getStudentUsername()).orElse(new User());

		// checking that both the password and the confirm password field are the same
		if (!newStudentPassword.isEmpty() && !confirmStudentPassword.isEmpty()) {
			if (!newStudentPassword.equals(confirmStudentPassword)) {
				model.addAttribute("passwordError", "Passwords do not match");
				return "av-edit-student";
			}

			String encryptedPassword = passwordEncoder.encode(newStudentPassword);
			Updatestudent.setStudentPassword(encryptedPassword);

			// updating the users password
			user.setPassword(encryptedPassword);
		}

		// updating the users username and email to match the student
		user.setUsername(Updatestudent.getStudentUsername());
		user.setEmail(Updatestudent.getStudentEmail());
		userRepository.save(user); // Save the user to userRepository

		// Debugging: Print the received student data
		System.out.println("Received Student Data:");
		System.out.println("ID: " + Updatestudent.getStudentId());
		System.out.println("First Name: " + Updatestudent.getStudentFirstName());
		System.out.println("Last Name: " + Updatestudent.getStudentLastName());
		System.out.println("Email: " + Updatestudent.getStudentEmail());
		System.out.println("Path Variable ID: " + Updatestudent.getStudentId());

		Updatestudent.getCourses().addAll(studentCourses);

		studentRepository.save(Updatestudent);
		return "av-edit-confirmation";
	}
	
	@GetMapping("/student/delete/{id}")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteStudentAV(@PathVariable("id") long id, Model model) {
        Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        System.out.println(student.getCourses());
        student.getCourses().clear();
        studentRepository.save(student);
        
        studentRepository.delete(student);
        return "av-edit-confirmation";
    }

	@GetMapping("/registration-confirmation")
	public String registerConfirm() {
		return "registration-confirmation"; // The HTML file
	}
	
	@GetMapping("/av-registration-confirmation")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public String registerConfirmAV() {
		return "av-registration-confirmation"; // The HTML file
	}

	@GetMapping("/course-success-page")
	public String showCourseSuccessForm() {
		return "course-success-page";
	}

	@GetMapping("/instructor-success")
	public String showInstructorSuccessForm() {
		return "/instructor-success";
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
	
	@GetMapping("/av-edit-instructor/{id}")
	public String showUpdateFormInstructorAV(@PathVariable("id") long id, Model model) {
	    Instructor instructor = instructorRepository.findById(id)
	      .orElseThrow(() -> new IllegalArgumentException("Invalid instructor Id:" + id));
	    
	    model.addAttribute("instructor", instructor);
	    return "av-edit-instructor"; 
	}
	
	@GetMapping("/instructor/delete/{id}")
	public String deleteInstructorAV(@PathVariable("id") long id, Model model) {
	    Instructor instructor = instructorRepository.findById(id)
	      .orElseThrow(() -> new IllegalArgumentException("Invalid instructor Id:" + id));
	    instructorRepository.delete(instructor);
	    return "av-instructor-edit-confirmation";
	}

	@Transactional
	@PostMapping("/av-edit-instructor/{id}")
	public String updateInstructorAV(@PathVariable("id") long id, @Validated Instructor instructor, 
	  BindingResult result, Model model, @RequestParam("newPassword") String newInstructorPassword, 
	  @RequestParam("confirmPassword") String confirmInstructorPassword) {
	    if (result.hasErrors()) {
	        instructor.setInstructorId(id); 
	        return "av-edit-instructor";
	    }
	    
	    
	    // Fetch the user (or create a new one if not found)
	    User user = userRepository.findByUsername(instructor.getInstructorUsername())
	            .orElse(new User());  

	    // Only process password if both fields are not empty
	    if (!newInstructorPassword.isEmpty() && !confirmInstructorPassword.isEmpty()) {
	        // Validate the new password and confirm password
	        if (!newInstructorPassword.equals(confirmInstructorPassword)) {
	            model.addAttribute("passwordError", "Passwords do not match");
	            return "av-edit-instructor";
	        }
	        
	        String encryptedPassword = passwordEncoder.encode(newInstructorPassword);
	        instructor.setInstructorPassword(encryptedPassword);
	        
	        // Update user's password
	        user.setPassword(encryptedPassword);
	    }

	    // Update other user properties
	    user.setUsername(instructor.getInstructorUsername());
	    user.setEmail(instructor.getInstructorEmail());
	    userRepository.save(user);  // Save the user to userRepository

	    // Save the instructor
	    instructorRepository.save(instructor);
	    
	    // Debugging: Print the received instructor data
	    System.out.println("Received Instructor Data:");
	    System.out.println("ID: " + instructor.getInstructorId());
	    System.out.println("First Name: " + instructor.getInstructorFirstName());
	    System.out.println("Last Name: " + instructor.getInstructorLastName());
	    System.out.println("Email: " + instructor.getInstructorEmail());
	    System.out.println("Path Variable ID: " + id);
	    
	    return "av-instructor-edit-confirmation"; 
	}
	
	@GetMapping("/list-instructors-av")
	public String showInstructorsAV(Model model) {
        List<Instructor> instructors = instructorRepository.findAll();
        model.addAttribute("instructors", instructors);
        return "av-instructor-list";
    }
	
	@GetMapping("/manager/delete/{id}")
    public String deleteScheduleManagerAV(@PathVariable("id") long id, Model model) {
        ScheduleManager scheduleManager = SMRepo.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        SMRepo.delete(scheduleManager);
        return "av-schedule-manager-edit-confirmation";
    }
	
	@GetMapping("/av-edit-schedule-manager/{id}")
	public String showUpdateFormScheduleManagerAV(@PathVariable("id") long id, Model model) {
	    ScheduleManager scheduleManager = SMRepo.findById(id)
	      .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule Manager Id:" + id));
	    
	    model.addAttribute("scheduleManager", scheduleManager);
	    return "av-edit-schedule-manager"; 
	}
	
	@Transactional
	@PostMapping("/av-edit-schedule-manager/{id}")
	public String updateScheduleManagersAV(@PathVariable("id") long id, @Validated ScheduleManager manager, 
	  BindingResult result, Model model, @RequestParam("newPassword") String newManagerPassword, 
	  @RequestParam("confirmPassword") String confirmManagerPassword) {
	    if (result.hasErrors()) {
	    	manager.setManagerId(id); 
	        return "av-edit-schedule-manager";
	    }
	    
	    // Fetch the user (or create a new one if not found)
	    User user = userRepository.findByUsername(manager.getManagerUsername())
	            .orElse(new User());  
	    
	    // Only process password if both fields are not empty
	    if (!newManagerPassword.isEmpty() && !confirmManagerPassword.isEmpty()) {
	        // Validate the new password and confirm password
	        if (!newManagerPassword.equals(confirmManagerPassword)) {
	            model.addAttribute("passwordError", "Passwords do not match");
	            return "av-edit-schedule-manager";
	        }
	        
	        String encryptedPassword = passwordEncoder.encode(newManagerPassword);
	        manager.setManagerPassword(encryptedPassword);
	        
	        // Update user's password
	        user.setPassword(encryptedPassword);
	    }

	    // Update other user properties
	    user.setUsername(manager.getManagerUsername());
	    user.setEmail(manager.getManagerEmail());
	    userRepository.save(user);  // Save the user to userRepository

	    // Save the manager
	    SMRepo.save(manager);
	    
	    // Debugging: Print the received instructor data
	    System.out.println("Received Instructor Data:");
	    System.out.println("ID: " + manager.getManagerId());
	    System.out.println("First Name: " + manager.getManagerFirstName());
	    System.out.println("Last Name: " + manager.getManagerLastName());
	    System.out.println("Email: " + manager.getManagerEmail());
	    System.out.println("Path Variable ID: " + id);
	    
	    return "av-schedule-manager-edit-confirmation"; 
	}

	
	@GetMapping("/list-schedule-managers-av")
	public String showScheduleManagersAV(Model model) {
        List<ScheduleManager> scheduleManager = SMRepo.findAll();
        model.addAttribute("scheduleManager", scheduleManager);
        return "av-schedule-manager-list";
    }
	
}