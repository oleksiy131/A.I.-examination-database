package edu.sru.thangiah.controller;

import java.util.ArrayList; 
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.RoleRepository;
import edu.sru.thangiah.repository.StudentRepository;
import edu.sru.thangiah.repository.UserRepository;

@Controller
@RequestMapping("/instructor")
public class InstructorController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@RequestMapping("/instructor_homepage")
	public String showInstructorHomepage() {
		return "instructor_homepage";
	}

    private  InstructorRepository instructorRepository;
    private  CourseRepository courseRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    public InstructorController(InstructorRepository instructorRepository, CourseRepository courseRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
    }
    
 
    @GetMapping("/list")
    public String showInstructorList(Model model) {
        List<Instructor> instructors = (List<Instructor>) instructorRepository.findAll();
        model.addAttribute("instructors", instructors);
        return "instructor-list"; // Create an HTML template for instructor list
    }
    
    @GetMapping("/generate-exam")
    public String showExamGenerator(Model model) {
        return "exam-generator"; // Create an HTML template for exam generation
    }
    
    @GetMapping("/pick-exam")
    public String pickExam(Model model) {
        return "pick-exam"; // Create an HTML template for exam generation
    }

    @PostMapping("/instructor/create")
    public ResponseEntity<String> createInstructor(@RequestBody Instructor instructor) {
        try {
            Instructor savedInstructor = instructorRepository.save(instructor);
            return ResponseEntity.ok("Instructor created successfully. Instructor ID: " + savedInstructor.getInstructorId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating instructor: " + e.getMessage());
        }
    }
    
    @GetMapping("/{instructorId}")
    public Instructor findInstructor(@PathVariable Long instructorId) {
        return instructorRepository.findById(instructorId).orElse(null);
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
	
	@GetMapping("/iv-edit-student/{id}")
    public String showUpdateFormIV(@PathVariable("id") long id, Model model) {
		Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        model.addAttribute("student", student);
        return "iv-edit-student";
    }
	
	@PostMapping("/update/{id}")
    public String updateStudent(@PathVariable("id") long id, @Validated Student student, 
      BindingResult result, Model model) {
        if (result.hasErrors()) {
            student.setStudentId(id);
            return "update-user";
        }
        
        // Debugging: Print the received student data
        System.out.println("Received Student Data:");
        System.out.println("ID: " + student.getStudentId());
        System.out.println("First Name: " + student.getStudentFirstName());
        System.out.println("Last Name: " + student.getStudentLastName());
        System.out.println("Email: " + student.getStudentEmail());
        System.out.println("Path Variable ID: " + id);
        
//        student.setStudentId(id);
//        student.setRole(student.getRole());
//        student.setStudentPassword(student.getStudentPassword());
        studentRepository.save(student);
        return "iv-edit-confirmation";
    }
	
	@GetMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable("id") long id, Model model) {
        Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        studentRepository.delete(student);
        return "edit-confirmation";
    }
	
	@GetMapping("/iv-student-list")
	@PreAuthorize("hasRole('SCHEDULE_MANAGER')")
	public String showStudentsListIV(Model model) {
		// Retrieve the list of students from the repository
		List<Student> students = (List<Student>) studentRepository.findAll();

		// Add the list of students to the model for rendering in the HTML template
		model.addAttribute("students", students);

		// Return the name of the HTML template to be displayed
		return "iv-student-list";
	}
    
	@GetMapping("/iv-create-student")
	public String showCreateStudentFormIV() {
		return "iv-create-student"; // This corresponds to the name of your HTML file
	}
	
    @Transactional
	@PostMapping("/createIV")
	public String createIV(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
	    System.out.println("Inside student-createIV method");
	    try {
	        // Check if the student with the given username already exists
	        if (studentRepository.findByStudentUsername(student.getStudentUsername()).isPresent()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Student with given username already exists.");
	            return "redirect:/create";
	        }

	        // Fetch the role with ID 2 and set it to the student
	        Roles roles = roleRepository.findById(2L)
	            .orElseThrow(() -> new RuntimeException("Role with ID 2 not found"));
	        List<Roles> rolesList = new ArrayList<>();
            rolesList.add(roles);
            student.setRoles(rolesList);

	        // Save the new student
	        studentRepository.save(student);

	        // Create and save the corresponding user
	        User newUser = new User();
	        newUser.setId(student.getStudentId());
	        newUser.setUsername(student.getStudentUsername());
	        String hashedPassword = passwordEncoder.encode(student.getStudentPassword());
		    newUser.setPassword(hashedPassword);
	        newUser.setRoles(rolesList);


            // Set enabled for the user as well
            newUser.setEnabled(true);

            userRepository.save(newUser);

	        redirectAttributes.addFlashAttribute("successMessage", "Student and corresponding user added successfully.");
	        return "iv-upload-success";
	    } catch (Exception e) {
	        System.out.println("Failed to add student: " + e.getMessage());
	        redirectAttributes.addFlashAttribute("errorMessage", "Failed to add student.");
	        return "redirect:/fail";
	    }
	}
    @PostMapping("/iv-update/{id}")
    public String updateStudentIV(@PathVariable("id") long id, @Validated Student student, 
      BindingResult result, Model model) {
        if (result.hasErrors()) {
            student.setStudentId(id);
            return "iv-update-user";
        }
        
        // Debugging: Print the received student data
        System.out.println("Received Student Data:");
        System.out.println("ID: " + student.getStudentId());
        System.out.println("First Name: " + student.getStudentFirstName());
        System.out.println("Last Name: " + student.getStudentLastName());
        System.out.println("Email: " + student.getStudentEmail());
        System.out.println("Path Variable ID: " + id);
        
//        student.setStudentId(id);
//        student.setRole(student.getRole());
//        student.setStudentPassword(student.getStudentPassword());
        studentRepository.save(student);
        return "iv-edit-confirmation";
    }
	
//	@GetMapping("/student/delete/{id}")
//    public String deleteStudentSMV(@PathVariable("id") long id, Model model) {
//        Student student = studentRepository.findById(id)
//          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
//        studentRepository.delete(student);
//        return "iv-edit-confirmation";
//    }
   
}


