package edu.sru.thangiah.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.ScheduleManager;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.RoleRepository;
import edu.sru.thangiah.repository.StudentRepository;
import edu.sru.thangiah.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequestMapping("/student/course")
public class StudentController 
{
    @Autowired
	private StudentRepository studentRepository;
    @Autowired
	private CourseRepository courseRepository;
    @Autowired
	private UserRepository userRepository;
    @Autowired
	private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

	public StudentController(StudentRepository studentRepository, CourseRepository courseRepository, UserRepository userRepository) {
        super();
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }
	
	@RequestMapping("/student_homepage")
	public String showStudentHomepage() {
		return "student_homepage";
	}

    @GetMapping("/students")
    public String showStudentList(Model model) {
        // Retrieve the list of students from the repository
        List<Student> students = studentRepository.findAll();

        // Add the list of students to the model for rendering in the HTML template
        model.addAttribute("students", students);

        // Return the name of the HTML template to be displayed
        return "student-list";
    }
    
	@PostMapping
	public Student saveStudentWithCourse(@RequestBody Student student) {
		return studentRepository.save(student);
	}
	
	
	@GetMapping("/{studentId}")
	public Student findStudent(@PathVariable Long studentId) {
		return studentRepository.findById(studentId).orElse(null);
	}
	
	@GetMapping("/find/{name}")
	public List<Student> findStudentsContainingByStudentFirstName(@PathVariable String name){
		return studentRepository.findBystudentFirstNameContaining(name);
	}
	
	@GetMapping("/search")
	public List<Course> findByIdContaining(@PathVariable Long id){
		return courseRepository.findByIdContaining(id);
	}
	
	@GetMapping("/math-quiz")
    public String mathQuizPage() {
        // displays the math quiz
        return "math-quiz"; // the name of the HTML template for the quiz page
    }
	
	@GetMapping("/sv-account-management")
	public String accountManager(Model model){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		System.out.println(auth.getName());
		System.out.println(auth.getPrincipal());
		System.out.println(auth.getDetails());
		
		String studentUser = auth.getName();
		
		List<Student> student = studentRepository.findBystudentUsernameContaining(studentUser);
		
		//System.out.println(CurrentManager.);
		
		model.addAttribute("student", student);
					
		return "sv-account-management";
	}
	
	@GetMapping("/sv-edit-current-student/{id}")
	public String editingCurrentUser(@PathVariable("id") long id, Model model) {
    	Student student = studentRepository.findById(id)
    		      .orElseThrow(() -> new IllegalArgumentException("Invalid instructor Id:" + id));
    		    
    		    model.addAttribute("student", student);
    		    
	    return "sv-edit-current-student"; 
	}
	
	@Transactional
	@PostMapping("/sv-edit-student/{id}")
	public String saveCurrentUserEdits(@PathVariable("id") long id, @Validated Student student, 
		      BindingResult result, Model model, @RequestParam("newPassword") String newStudentPassword, 
			  @RequestParam("confirmPassword") String confirmStudentPassword) {
		        if (result.hasErrors()) {
		            student.setStudentId(id);
		            return "sv-edit-current-student";
		        }
		        
		     // checking the user to exist and creating it if it does not already exist
		        User user = userRepository.findByUsername(student.getStudentUsername())
		                .orElse(new User());  

		        // checking that both the password and the confirm password field are the same
		        if (!newStudentPassword.isEmpty() || !confirmStudentPassword.isEmpty()) {
		            if (!newStudentPassword.equals(confirmStudentPassword)) {
		                model.addAttribute("passwordError", "Passwords do not match");
		                return "sv-edit-current-student";
		            }
		            
		            String encryptedPassword = passwordEncoder.encode(newStudentPassword);
		            student.setStudentPassword(encryptedPassword);
		            
		            // updating the users password
		            user.setPassword(encryptedPassword);
		        }

		        // updating the users username and email to match the student
		        user.setUsername(student.getStudentUsername());
		        user.setEmail(student.getStudentEmail());
		        userRepository.save(user);  // Save the user to userRepository

		        
		        // Debugging: Print the received student data
		        System.out.println("Received Student Data:");
		        System.out.println("ID: " + student.getStudentId());
		        System.out.println("First Name: " + student.getStudentFirstName());
		        System.out.println("Last Name: " + student.getStudentLastName());
		        System.out.println("Email: " + student.getStudentEmail());
		        System.out.println("Path Variable ID: " + id);
		        
//		        student.setStudentId(id);
//		        student.setRole(student.getRole());
//		        student.setStudentPassword(student.getStudentPassword());
		        studentRepository.save(student);
	    
	    return "sv-student-edit-confirmation"; 
	}
	

}
