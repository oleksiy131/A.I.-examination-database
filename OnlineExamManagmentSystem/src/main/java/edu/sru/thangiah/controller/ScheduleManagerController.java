package edu.sru.thangiah.controller;
 

import edu.sru.thangiah.domain.Course;

import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.ScheduleManager;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.exception.ResourceNotFoundException;
import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.RoleRepository;
import edu.sru.thangiah.repository.ScheduleManagerRepository;
import edu.sru.thangiah.repository.StudentRepository;
import edu.sru.thangiah.repository.UserRepository;
import edu.sru.thangiah.service.ExcelExportService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;



@Controller
@RequestMapping("/schedule-manager")
public class ScheduleManagerController {
	
	
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ScheduleManagerRepository scheduleManagerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ExcelExportService excelExportService;
    
	@RequestMapping("/schedule_manager_homepage")
	public String showScheduleManagerHomepage() {
		
		return "schedule_manager_homepage";
	}
	
	
	@GetMapping("/smv-account-management")
	public String passwordReset(Model model){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		System.out.println(auth.getName());
		System.out.println(auth.getPrincipal());
		System.out.println(auth.getDetails());
		
		String managerUser = auth.getName();
		
		List<ScheduleManager> manager = scheduleManagerRepository.findByManagerUsernameContaining(managerUser);
		
		//System.out.println(CurrentManager.);
		
		model.addAttribute("manager", manager);
					
		return "smv-password-reset";
	}
	

	@GetMapping("/smv-edit-current-manager/{id}")
	public String editingCurrentUser(@PathVariable("id") long id, Model model) {
	    ScheduleManager manager = scheduleManagerRepository.findById(id)
	      .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule Manager Id:" + id));
	    
	    model.addAttribute("manager", manager);
	    return "smv-edit-current-manager"; 
	}
	
	@Transactional
	@PostMapping("/smv-edit-current-user/{id}")
	public String saveCurrentUserEdits(@PathVariable("id") long id, @Validated ScheduleManager manager, 
	  BindingResult result, Model model, @RequestParam(value = "currentPassword", required = false) String currentPassword, @RequestParam(value = "newPassword", required = false) String newManagerPassword, 
	  @RequestParam(value="confirmPassword", required=false) String confirmManagerPassword) {
	    if (result.hasErrors()) {
	    	manager.setManagerId(id); 
	        return "smv-edit-current-manager";
	    }
	    
	    // Fetch the user (or create a new one if not found)
	    User user = userRepository.findByUsername(manager.getManagerUsername())
	            .orElse(new User());
	    
	    boolean passwordError = false;

	    // Only process passwords if new password fields are filled
	    if (newManagerPassword != null && !newManagerPassword.isEmpty()) {
	        // Verify current password
	        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
	            model.addAttribute("passwordError", "Current password is incorrect");
	            passwordError = true;
	        } 
	        // Check if new passwords match
	        else if (!newManagerPassword.equals(confirmManagerPassword)) {
	            model.addAttribute("passwordError", "New passwords do not match");
	            passwordError = true;
	        } 
	        // Process password update if there's no error
	        else {
	            String encryptedPassword = passwordEncoder.encode(newManagerPassword);
	            manager.setManagerPassword(encryptedPassword);
	            user.setPassword(encryptedPassword);
	            userRepository.save(user);  // Save the user to userRepository
	        }
	    }

	    // Save the manager regardless of password change
	    scheduleManagerRepository.save(manager);  // Save the manager

	    // If there was a password error, re-display the form
	    if (passwordError) {
	        model.addAttribute("manager", manager);
	        return "smv-edit-current-manager";
	    }

	    // If everything is fine, redirect to the confirmation page
	    return "smv-user-edit-confirmation"; 
	}

	

	
    @GetMapping("/add-course")
    public String showCreateCourseForm() {
        return "add-course"; 
    }

    @GetMapping("/add-courseSM")
    public String showCreateCourseFormSMV() {
        return "smv-add-course"; 
    }
    

    @GetMapping("/instructor-list")
    public String showInstructorList(Model model) {
        List<Instructor> instructors = instructorRepository.findAll();
        model.addAttribute("instructors", instructors);
        return "instructor-list";
    }
    

    @GetMapping("/create-instructor")
    public String showCreateInstructorForm() {
        return "create-instructor"; 
    }
    
	@GetMapping("/associateSM")
	public String associateStudentWithCourseFormSMV(Model model) {
		// Retrieve the list of students and courses from the repository
		List<Student> students = studentRepository.findAll();
		List<Course> courses = courseRepository.findAll();

		// Add the lists of students and courses to the model for rendering in the HTML
		// template
		model.addAttribute("students", students);
		model.addAttribute("courses", courses);

		// Return the name of the HTML template for the form
		return "smv-associate-students";
	}
	
	@PostMapping("/associateSM")
    public String handleAssociateStudentWithCourse(@RequestParam("studentId") Long studentId, @RequestParam("courseId") Long courseId) {
        System.out.println(studentId);
        if(studentId == null || courseId == null) {
            // Redirect back to the form with an error message
            return "redirect:/associateSM?error=Please select both a student and a course.";
        }

        
         Student student = studentRepository.findById(studentId).orElse(null);

         Course course = courseRepository.findById(courseId).orElse(null);;
        

        
         if(student != null && course != null) {
        	// Associate the student with the course

            student.getCourses().add(course);
            studentRepository.save(student);
            return "smv-student-association-confirmation";
        } 
        else {
			return "smv-student-association-fail";
		}

    }

	
	@GetMapping("/associate-instructorSM")
	public String associateInstructorWithCourseFormSMV(Model model) {
		// Retrieve the list of instructors and courses from the repository
		List<Instructor> instructors = instructorRepository.findAll();
		List<Course> courses = courseRepository.findAll();

		// Add the lists of instructors and courses to the model for rendering in the
		// HTML template
		model.addAttribute("instructors", instructors);
		model.addAttribute("courses", courses);

		// Return the name of the HTML template for the form
		return "smv-associate-instructor";
	}
	@PostMapping("/associate-instructorSM")
	public String handleAssociateInstructorWithCourse(@RequestParam("instructorId") Long instructorId, @RequestParam("courseId") Long courseId) {
		System.out.println(instructorId);
		 if(instructorId == null || courseId == null) {
		        // Redirect back to the form with an error message
		        return "redirect:/associate-instructorSM?error=Please select both an instructor and a course.";
		    }

        
        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);

        Course course = courseRepository.findById(courseId).orElse(null);;

       
        if(instructor != null && course != null) {

    		// Associate instructor with the course
    		//instructor = instructor.get();
    		course.setInstructor(instructor);
    		courseRepository.save(course);
           return "smv-instructor-association-confirmation";
       } 
       else {
			return "smv-instructor-association-fail";
		}
	}
    

	
    @PreAuthorize("hasRole('SCHEDULE_MANAGER')")
    @GetMapping("/create-instructors")
    public String showCreateInstructorFormSMV() {
        return "smv-create-instructor"; 
    }
    
	@GetMapping("/list-students")
	@PreAuthorize("hasRole('SCHEDULE_MANAGER')")
	public String showStudentsListSMV(Model model) {
		// Retrieve the list of students from the repository
		List<Student> students = (List<Student>) studentRepository.findAll();

		// Add the list of students to the model for rendering in the HTML template
		model.addAttribute("students", students);

		// Return the name of the HTML template to be displayed
		return "smv-student-list";
	}
    
    
    @GetMapping("/instructors")
    @ResponseBody
    public List<Instructor> getInstructors() {
        return instructorRepository.findAll();
    }
    
    @GetMapping("/all-sm")
    public String showSMSMV(Model model) {
        List<ScheduleManager> ScheduleManager = scheduleManagerRepository.findAll();
        model.addAttribute("ScheduleManager", ScheduleManager);
        return "smv-schedule-manager-list";
    }
    
    @GetMapping("/all")
    public String showSM(Model model) {
        List<ScheduleManager> ScheduleManager = scheduleManagerRepository.findAll();
        model.addAttribute("ScheduleManager", ScheduleManager);
        return "schedule-manager-list";
    }

    
	/*
	 * @GetMapping("/associate-instructor") public String
	 * associateInstructorWithCourseForm(Model model) { // Retrieve the list of
	 * instructors and courses from the repository List<Instructor> instructors =
	 * instructorRepository.findAll(); List<Course> courses =
	 * courseRepository.findAll();
	 * 
	 * // Add the lists of instructors and courses to the model for rendering in the
	 * HTML template model.addAttribute("instructors", instructors);
	 * model.addAttribute("courses", courses);
	 * 
	 * return "associate-instructor"; }
	 */
    
	/*
	 * @PostMapping("/associate-instructor") public String
	 * associateInstructorWithCourse(
	 * 
	 * @RequestParam Long instructorId,
	 * 
	 * @RequestParam Long courseId, RedirectAttributes redirectAttributes) {
	 * 
	 * Optional<Instructor> optionalInstructor =
	 * instructorRepository.findById(instructorId); Optional<Course> optionalCourse
	 * = courseRepository.findById(courseId);
	 * 
	 * if (optionalInstructor.isPresent() && optionalCourse.isPresent()) {
	 * Instructor instructor = optionalInstructor.get(); Course course =
	 * optionalCourse.get();
	 * 
	 * // Assuming the association is bidirectional
	 * instructor.getCourses().add(course); course.setInstructor(instructor);
	 * 
	 * instructorRepository.save(instructor); courseRepository.save(course);
	 * redirectAttributes.addFlashAttribute("successMessage",
	 * "Instructor successfully associated with the course"); } else {
	 * redirectAttributes.addFlashAttribute("failureMessage",
	 * "Failed to associate instructor with the course"); }
	 * 
	 * return "redirect:/success"; }
	 * 
	 */

    
    @GetMapping("/iv-edit-student/{id}")
    public String showUpdateFormIV(@PathVariable("id") long id, Model model) {
		Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        model.addAttribute("student", student);
        return "iv-edit-student";
    }

    
    
    @GetMapping("/page")
    public String loadManagerPage(Model model) {
        List<Instructor> instructors = instructorRepository.findAll();
        model.addAttribute("instructors", instructors);
        return "manager"; 
    }
    
    
	/*
	 * // Load/Create Instructor
	 * 
	 * @Transactional
	 * 
	 * @PostMapping("/schedule-manager/add") public String
	 * addInstructor(@ModelAttribute Instructor instructor, RedirectAttributes
	 * redirectAttributes) { System.out.println("Inside instructor-add method"); try
	 * { // Check if the instructor with the given username already exists if
	 * (instructorRepository.findByInstructorUsername(instructor.
	 * getInstructorUsername()).isPresent()) {
	 * redirectAttributes.addFlashAttribute("errorMessage",
	 * "Instructor with given username already exists."); return
	 * "redirect:/schedule-manager/create-instructor"; }
	 * 
	 * // Fetch the role with ID 3 and set it to the instructor Roles roles =
	 * roleRepository.findById(3L) .orElseThrow(() -> new
	 * RuntimeException("Role with ID 3 not found")); List<Roles> rolesList = new
	 * ArrayList<>(); rolesList.add(roles); instructor.setRoles(rolesList);
	 * 
	 * // Save the new instructor instructorRepository.save(instructor);
	 * 
	 * // Create and save the corresponding user User newUser = new User();
	 * newUser.setId(instructor.getInstructorId());
	 * newUser.setUsername(instructor.getInstructorUsername()); String
	 * hashedPassword = passwordEncoder.encode(instructor.getInstructorPassword());
	 * newUser.setPassword(hashedPassword);
	 * 
	 * newUser.setRoles(rolesList);
	 * 
	 * 
	 * // Set enabled for the user as well newUser.setEnabled(true);
	 * 
	 * userRepository.save(newUser);
	 * 
	 * redirectAttributes.addFlashAttribute("successMessage",
	 * "Instructor and corresponding user added successfully."); return
	 * "redirect:/instructor-success"; } catch (Exception e) {
	 * System.out.println("Failed to add instructor: " + e.getMessage());
	 * redirectAttributes.addFlashAttribute("errorMessage",
	 * "Failed to add instructor."); return "redirect:/fail"; } }
	 */

	@GetMapping("/smv-instructor-success")
	public String showInstructorSuccessFormSMV() {
		return "smv-instructor-success";
	}
    
    // Load/Create Instructor
    @Transactional
    @PostMapping("/add")
    public String addInstructorSMV(@ModelAttribute Instructor instructor, RedirectAttributes redirectAttributes) {
        System.out.println("Inside instructor-addSMV method");
        try {
            // Check if the instructor with the given username already exists
            if (instructorRepository.findByInstructorUsername(instructor.getInstructorUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Instructor with given username already exists.");
                return "redirect:/schedule-manager/smv-create-instructor";
            }

            // Fetch the role with ID 3 and set it to the instructor
            Roles roles = roleRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Role with ID 3 not found"));
            List<Roles> rolesList = new ArrayList<>();
            rolesList.add(roles);
            instructor.setRoles(rolesList);

            // Save the new instructor
            instructorRepository.save(instructor);

            // Create and save the corresponding user
            User newUser = new User();
            newUser.setId(instructor.getInstructorId());
            newUser.setUsername(instructor.getInstructorUsername());
            String hashedPassword = passwordEncoder.encode(instructor.getInstructorPassword());
    	    newUser.setPassword(hashedPassword);

    	    newUser.setRoles(rolesList); 


            // Set enabled for the user as well
            newUser.setEnabled(true);

            userRepository.save(newUser);

            redirectAttributes.addFlashAttribute("successMessage", "Instructor and corresponding user added successfully.");
            return "redirect:/schedule-manager/smv-instructor-success";
        } catch (Exception e) {
            System.out.println("Failed to add instructor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add instructor.");
            return "redirect:/fail";
        }
    }
    
    @GetMapping("smv-course-success-page")
    public String showSuccessPageSMV() {
        return "smv-course-success-page";  // This should be the name of your Thymeleaf template (without .html)
    }

    
    @PreAuthorize("hasRole('SCHEDULE_MANAGER')")
    @PostMapping("/add-courseSM")
    public String addCourseSM(@ModelAttribute Course course, Model model) {
        System.out.println("Inside addCourseSM method");  // Debugging line
        try {
            // Save the course to the database
            Course savedCourse = courseRepository.save(course);
            if (savedCourse != null) {
                model.addAttribute("message", "Course added successfully.");
                return "redirect:/schedule-manager/smv-course-success-page"; // Redirect to a success page
            } else {
                model.addAttribute("message", "Error adding course. Controller");
                return "add-courseSM"; // Stay on the same page and display the error
            }
        } catch (Exception e) {
            model.addAttribute("message", "Error adding course. Controller Error");
            return "add-courseSM"; // Stay on the same page and display the error
        }
    }


	/*
	 * // Load/Create Course
	 * 
	 * @PreAuthorize("hasRole('SCHEDULE_MANAGER')")
	 * 
	 * @PostMapping("/add-course-api") public ResponseEntity<?>
	 * addCourseSM(@ModelAttribute Course course){ try { Course savedCourse =
	 * courseRepository.save(course); return ResponseEntity.ok().
	 * body("{\"success\": true, \"message\": \"Course added.\"}"); } catch
	 * (Exception e) { return ResponseEntity.badRequest().
	 * body("{\"success\": false, \"message\": \"Failed to add course.\"}"); } }
	 */
    
    
    // Load/Create Course
    @PostMapping("/course/add")
    public ResponseEntity<?> addCourse(@ModelAttribute Course course){
        try {
            Course savedCourse = courseRepository.save(course);
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Course added.\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Failed to add course.\"}");
        }
    }

    // Assign instructor to course
    @PostMapping("/course/assign-instructor")
    public ResponseEntity<?> assignInstructorToCourse(
        @RequestParam Long courseId, 
        @RequestParam Long instructorId) {
        try {
            Course course = courseRepository.findById(courseId).orElse(null);
            Instructor instructor = instructorRepository.findById(instructorId).orElse(null);

            if (course == null || instructor == null) {
                return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Invalid course or instructor ID.\"}");
            }

            course.setInstructor(instructor);
            courseRepository.save(course);

            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Instructor assigned to course.\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Failed to assign instructor.\"}");
        }
    }

    @GetMapping("/smv-edit-instructor/{id}")
    public String showInstructorUpdateFormSMV(@PathVariable("id") long id, Model model) {
    	Instructor instructor = instructorRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        model.addAttribute("instructor", instructor);
        return "smv-edit-instructor";
    }
    
    @PostMapping("/update/{id}")
    public String updateInstructorSMV(@PathVariable("id") long id, @Validated Instructor instructor, 
      BindingResult result, Model model) {
        if (result.hasErrors()) {
        	instructor.setInstructorId(id);
            return "update-user";
        }
        
        // Debugging: Print the received student data
        System.out.println("Received Instructor Data:");
	    System.out.println("ID: " + instructor.getInstructorId());
	    System.out.println("First Name: " + instructor.getInstructorFirstName());
	    System.out.println("Last Name: " + instructor.getInstructorLastName());
	    System.out.println("Email: " + instructor.getInstructorEmail());
	    System.out.println("Path Variable ID: " + id);
        
//        student.setStudentId(id);
//        student.setRole(student.getRole());
//        student.setStudentPassword(student.getStudentPassword());
	    instructorRepository.save(instructor);
        return "smv-edit-instructor-confirmation";
    }

    // Delete instructor
    @Transactional
    @GetMapping("/instructor/delete/{id}")
	public String deleteInstructorSMV(@PathVariable("id") long id, Model model) {
	    Instructor instructor = instructorRepository.findById(id)
	      .orElseThrow(() -> new IllegalArgumentException("Invalid instructor Id:" + id));
	    
	    // Get all courses associated with the instructor
	    Set<Course> courses = instructor.getCourses();
	    
	    if (courses != null) {
	        // Iterate over the courses and set the instructor to null
	        for (Course course : courses) {
	            course.setInstructor(null);
	            // Save the course to update the association in the database
	            courseRepository.save(course);
	        }
	    }
  
     
        instructorRepository.delete(instructor);
     
     return "smv-edit-instructor-confirmation";
 }
    
    
	@GetMapping("/create-students")
	public String showCreateStudentFormSMV() {
		return "smv-create-student"; // This corresponds to the name of your HTML file
	}
    
    @Transactional
	@PostMapping("/create")
	public String createSMV(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
	    System.out.println("Inside student-create method");
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
	        return "smv-upload-success";
	    } catch (Exception e) {
	        System.out.println("Failed to add student: " + e.getMessage());
	        redirectAttributes.addFlashAttribute("errorMessage", "Failed to add student.");
	        return "redirect:/fail";
	    }
	}
    
	@GetMapping("/smv-edit-student/{id}")
    public String showUpdateFormSMV(@PathVariable("id") long id, Model model) {
		Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        model.addAttribute("student", student);
        return "smv-edit-student";
    }

	@PostMapping("/smv-update/{id}")
    public String updateStudentSMV(@PathVariable("id") long id, @Validated Student student, 
      BindingResult result, Model model, @RequestParam("newPassword") String newStudentPassword, 
	  @RequestParam("confirmPassword") String confirmStudentPassword) {
        if (result.hasErrors()) {
            student.setStudentId(id);
            return "smv-update-user";
        }
        
     // checking the user to exist and creating it if it does not already exist
        User user = userRepository.findByUsername(student.getStudentUsername())
                .orElse(new User());  

        // checking that both the password and the confirm password field are the same
        if (!newStudentPassword.isEmpty() && !confirmStudentPassword.isEmpty()) {
            if (!newStudentPassword.equals(confirmStudentPassword)) {
                model.addAttribute("passwordError", "Passwords do not match");
                return "smv-edit-student";
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
        
//        student.setStudentId(id);
//        student.setRole(student.getRole());
//        student.setStudentPassword(student.getStudentPassword());
        studentRepository.save(student);
        return "smv-edit-confirmation";
    }
	
	@GetMapping("/student/delete/{id}")
    public String deleteStudentSMV(@PathVariable("id") long id, Model model) {
        Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        //you will need to also remove the student from their courses or its going to delete all the students associated with that course
        
       	// remove the student from the courses

           System.out.println(student.getCourses());
           student.getCourses().clear();
           studentRepository.save(student);
           //return "smv-edit-confirmation";
     
        
           studentRepository.delete(student);
        
        return "smv-edit-confirmation";
    }
	
	@GetMapping("/exportStudentSMV")
	public ResponseEntity<String> exportStudentDataSMV() {
	    try {
	        // Fetches the list of students from the repository
	        List<Student> students = (List<Student>) studentRepository.findAll(); 

	        if (students != null && !students.isEmpty()) {
	            // Get the user's downloads folder
	            String userHome = System.getProperty("user.home");
	            String downloadsDirectory = userHome + File.separator + "Downloads";
	            
	            // Define the file path in the downloads folder
	            String filePath = downloadsDirectory + File.separator + "student_exported_data.xlsx";
	            
	            // Check if the file already exists
	            File file = new File(filePath);
	            boolean fileExists = file.exists();
	            
	            excelExportService.exportStudentData(students, filePath, fileExists);
	            
	            if (fileExists) {
	                return ResponseEntity.ok("Student data updated successfully!");
	            } else {
	                return ResponseEntity.ok("Student data exported successfully!");
	            }
	        } else {
	            return ResponseEntity.ok("No students found to export!");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to export student data!");
	    }
	}
	
	//NTS
	
	@GetMapping("/uploadExcelSMV")
	public String importStudentsSMV() {
		return "smv-import"; // This corresponds to the name of your HTML file
	}
	
	@GetMapping("/smv-upload-success")
	public String uploadSuccessSMV() {
		return "smv-upload-success"; // This corresponds to the name of your HTML file
	}
	
	@Transactional
	@PostMapping("/uploadExcelSMV")
	public String uploadExcelSMV(@RequestParam("file") MultipartFile file) {
	    if (file.isEmpty()) {
	        // Handle empty file error
	        return "redirect:/schedule-manager/smv-import?error=emptyfile";
	    }

	    try (InputStream inputStream = file.getInputStream()) {
	        Workbook workbook = WorkbookFactory.create(inputStream);
	        Sheet sheet = workbook.getSheetAt(0); // Assuming the data is on the first sheet

	        // Fetch the role with ID 2 once
	        Roles role = roleRepository.findById(2L)
	            .orElseThrow(() -> new RuntimeException("Role with ID 2 not found"));
	        List<Roles> rolesList = new ArrayList<>();

	        // Iterate through rows and columns to extract data
	        for (Row row : sheet) {
	            if (row.getRowNum() == 0) {
	                // Skip the header row
	                continue;
	            }

                Student student = new Student();


                // Handle each cell in the row
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                // Cell contains a string value
                                switch (i) {
                                    case 1:
                                        student.setStudentFirstName(cell.getStringCellValue());
                                        break;
                                    case 2:
                                        student.setStudentLastName(cell.getStringCellValue());
                                        break;
                                    case 3:
                                        student.setStudentEmail(cell.getStringCellValue());
                                        break;
                                    case 4:
                                        student.setStudentPassword(cell.getStringCellValue());
                                        break;
                                    case 5:
                                        student.setStudentUsername(cell.getStringCellValue());
                                        break;
                                    // Add cases for other cells as needed
                                }
                                break;
                            case NUMERIC:
                                // Cell contains a numeric value
                                double numericValue = cell.getNumericCellValue();
                                switch (i) {
                                    case 0:
                                        student.setStudentId((long) numericValue);
                                        System.out.println("Assigned Student ID: " + student.getStudentId());  // For debugging purposes

                                        break;
                                    case 6:
                                        // Assuming column 6 contains numeric value for Credits Taken
                                        student.setCreditsTaken((float) numericValue);
                                        break;
                                    // Handle numeric value for other cells if needed
                                }
                                break;
                        }
              
                    }
                }
                
                // Fetch the role with ID 2 and set it to the student
                if (student.getStudentId() == null) {
                    System.out.println("Skipped a row due to missing Student ID.");
                    continue;
                }

                student.setRoles(rolesList);

                Optional<Student> existingStudent = studentRepository.findById(student.getStudentId());
                if (!existingStudent.isPresent()) {
                    // Saving the student to the database since it doesn't exist
                    studentRepository.save(student);
                    User newUser = new User();
                    newUser.setId(student.getStudentId());
                    newUser.setUsername(student.getStudentUsername());
                    
                    // Encode the password before saving
                    String encodedPassword = passwordEncoder.encode(student.getStudentPassword());
                    newUser.setPassword(encodedPassword);
                    
                    newUser.setRoles(rolesList); 
                    newUser.setEnabled(true);
                    userRepository.save(newUser);
                } else {
                    // Handle the case when the student already exists, if needed
                }
            }

            workbook.close();  // Close the workbook
            return "redirect:/schedule-manager/smv-upload-success";

        } catch (IOException e) {
            e.printStackTrace();
            // Handle file processing error
            return "redirect:/instructor/smv-import?error=processing";
        }
    }
	
	@GetMapping("/smv-upload-fail")
	public String showSmvUploadFail() {
		return "smv-upload-fail"; // This corresponds to the name of your HTML file
	}
	
	@GetMapping("/smv-class-import")
	public String showSmvClassImport() {
		return "smv-class-import"; // This corresponds to the name of your HTML file
	}
	
	@PostMapping("/smv-upload")
	public String upload(@RequestParam("file") MultipartFile file) throws IOException {

		boolean instructorCreated = false;

		if (file.isEmpty()) {

			return "redirect:/schedule-manager/smv-import?error=emptyfile";
		}

		InputStream is = file.getInputStream();
		Workbook workbook = WorkbookFactory.create(is);

		Sheet sheet = workbook.getSheetAt(0);
		String courseNameFull = sheet.getRow(4).getCell(0).getStringCellValue();
		String instructorNameFull = sheet.getRow(2).getCell(0).getStringCellValue();
		String instructorEmailFull = sheet.getRow(1).getCell(0).getStringCellValue();
		String instructorIdString = sheet.getRow(3).getCell(0).getStringCellValue();
		String courseIdString = sheet.getRow(5).getCell(0).getStringCellValue();

		System.out.println(instructorIdString);
		System.out.println(instructorNameFull);
		System.out.println(courseNameFull);
		System.out.println(courseIdString);

		// converting courseId
		String courseIdShort = removeBeforeColon(courseIdString);
		long courseId = Long.parseLong(courseIdShort);

		System.out.println(courseId);
		String courseName = removeBeforeColon(courseNameFull);
		System.out.println(courseName);
		
		
		
		Course course = new Course();
		course.setId(courseId);
		course.setCourseName(courseName);
		courseRepository.save(course);

		
		
		String parsedInstructorName = removeBeforeColon(instructorNameFull);
		String[] instructorsNames = extractNames(parsedInstructorName);
		String InsFirstName = instructorsNames[1];
		String InsLastName = instructorsNames[0];

		// converting instructorId
		String instructorIdshort = removeBeforeColon(instructorIdString);
		long instructorId = Long.parseLong(instructorIdshort);

		// getting email for instructor
		String instructorEmail = removeBeforeColon(instructorEmailFull);
		String instructorUsername = parseEmail(instructorEmail);

		System.out.println(instructorId);
		System.out.println(InsFirstName);
		System.out.println(InsLastName);

		Instructor instructor = new Instructor();

		Optional<Instructor> existingInstructor = instructorRepository.findByInstructorUsername(instructorUsername);
		if (!existingInstructor.isPresent()) {

			instructor.setInstructorId(instructorId);
			instructor.setInstructorFirstName(InsFirstName);
			instructor.setInstructorLastName(InsLastName);
			instructor.setInstructorEmail(instructorEmail);
			instructor.setInstructorUsername(instructorUsername);
			String hashedPassword = passwordEncoder.encode("instructor");
			instructor.setInstructorPassword(hashedPassword);

			instructorRepository.save(instructor);

			instructorCreated = true;

			Roles roles = roleRepository.findById(3L)
					.orElseThrow(() -> new RuntimeException("Role with ID 4 not found"));
			List<Roles> rolesList = new ArrayList<>();
			rolesList.add(roles);
			instructor.setRoles(rolesList);

			Optional<User> existingUser = userRepository.findByUsername(instructor.getInstructorUsername());
			if (!existingUser.isPresent()) {
				User user = new User();
				user.setEmail(instructor.getInstructorEmail());
				user.setFirstName(instructor.getInstructorFirstName());
				user.setLastName(instructor.getInstructorLastName());
				user.setUsername(instructor.getInstructorUsername());
				user.setPassword(hashedPassword);
				user.setEnabled(true);
				rolesList.add(roles);
				user.setRoles(rolesList);
				userRepository.save(user);
			} else {
				System.out.println("Console LOG: User Id is already present in the database");
			}

			course.setInstructor(instructor);
			courseRepository.save(course);

		} else {
			// Associate instructor with the course
			instructor = existingInstructor.get();
			course.setInstructor(instructor);
			courseRepository.save(course);
		}

		// this section adds the students id's, names, emails, generates usernames and
		// default password

		try {
			for (int i = 8; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null) { // null check
					System.out.println("Encountered null row. Stopping processing.");
					break; // This will exit the loop when a null row is encountered.
				}

				Student student = new Student();
				String StuFirstName;
				String StuLastName;
				long StudentID;
				String studentEmail;

				if (row.getCell(0) != null) {
					StudentID = (long) row.getCell(0).getNumericCellValue();
					System.out.println(StudentID);
					student.setStudentId(StudentID);
				}

				if (row.getCell(1) != null) {
					String StudentName = row.getCell(1).getStringCellValue();
					String[] StudentNames = extractNames(StudentName);
					StuFirstName = StudentNames[1];
					StuLastName = StudentNames[0];
					student.setStudentFirstName(StuFirstName);
					student.setStudentLastName(StuLastName);
				}
				if (row.getCell(2) != null) {
					studentEmail = row.getCell(2).getStringCellValue();
					System.out.println(studentEmail);
					student.setStudentEmail(studentEmail);
					String username = parseEmail(studentEmail);
					student.setStudentUsername(username);
					System.out.println(username);
				}

				Roles roles = roleRepository.findById(2L)
						.orElseThrow(() -> new RuntimeException("Role with ID 4 not found"));
				List<Roles> rolesList = new ArrayList<>();
				rolesList.add(roles);
				student.setRoles(rolesList);

				student.setStudentPassword("student" + student.getStudentId());
				String hashedPassword = passwordEncoder.encode(student.getStudentPassword());
				student.setStudentPassword(hashedPassword);

				// Check if the student already exists in the database
				Optional<Student> existingStudent = studentRepository.findById(student.getStudentId());
				if (!existingStudent.isPresent()) {
					// Student does not exist, save it
					studentRepository.save(student);

					// Associate the student with the course
					student.getCourses().add(course);
					studentRepository.save(student);
				} else {
					System.out.println("Console LOG: Student Id is already present in the database");
				}

				Optional<User> existingUser = userRepository.findByUsername(student.getStudentUsername());
				if (!existingUser.isPresent()) {
					User user = new User();
					user.setEmail(student.getStudentEmail());
					user.setFirstName(student.getStudentFirstName());
					user.setLastName(student.getStudentLastName());
					user.setUsername(student.getStudentUsername());
					user.setPassword(hashedPassword);
					user.setEnabled(true);
					rolesList.add(roles);
					user.setRoles(rolesList);
					userRepository.save(user);
				} else {
					System.out.println("Console LOG: User Id is already present in the database");
				}

			}

			if (instructorCreated != true) {
				return "redirect:/schedule-manager/smv-upload-success";
			} else {
				return "redirect:/schedule-manager/smv-upload-fail";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}
	}
	    
	    public static String removeBeforeColon(String input) {
	        int colonIndex = input.indexOf(":");
	        if (colonIndex != -1) {
	            return input.substring(colonIndex + 1).trim();
	        } else {
	            // Return the original string if there is no colon in it.
	            return input;
	        }
	    }
	    
	    public static String[] extractNames(String fullName) {
	        // Split the full name using a comma and space as the delimiter
	        String[] names = fullName.split(",\\s+");
	        if (names.length == 2) {
	            return names; // Assuming the input format is correct (Last Name, First Name)
	        } else {
	            // Handle incorrect input format or provide default values as needed
	            return new String[]{"null", "null"};
	        }
	    }
	    
	    public static String parseEmail(String email) {
	        int atIndex = email.indexOf("@");
	        if (atIndex != -1) {
	            return email.substring(0, atIndex).trim();
	        } else {
	            // If there is no "@" symbol, return the original email.
	            return email;
	        }
	    }
	    
		@GetMapping("/delete/{id}")
		public String deleteClassSMV(@PathVariable("id") long id, Model model) {
		    Course course = courseRepository.findById(id)
		      .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
		    courseRepository.delete(course);
		    return "smv-edit-class-confirmation";
		}
		
		@GetMapping("/smv-class-list")
		public String showClassListSMV(Model model) {
			// Retrieve the list of students from the repository
			List<Course> course = (List<Course>) courseRepository.findAll();

			// Add the list of students to the model for rendering in the HTML template
			model.addAttribute("course", course);

			// Return the name of the HTML template to be displayed
			return "smv-class-list";
		}
		@GetMapping("/smv-edit-class/{id}")
		public String showUpdateClassSMV(@PathVariable("id") long id, Model model) {
		    Course course = courseRepository.findById(id)
		      .orElseThrow(() -> new IllegalArgumentException("Invalid Course Id:" + id));
		    
		    model.addAttribute("course", course);
		    return "smv-edit-class"; 
		}
		
		@PostMapping("/smv-edit-class/{id}")
	    public String updateClassSMV(@PathVariable("id") long id, @Validated Course course, 
	      BindingResult result, Model model) {
	        if (result.hasErrors()) {
	            course.setId(id);
	            return "smv-edit-class";
	        }
	        // Debugging: Print the received student data
	        System.out.println("Received Class Data:");
	        System.out.println("ID: " + course.getId());
	        System.out.println("Course Name: " + course.getCourseName());
	        System.out.println("Path Variable ID: " + id);
	        
	        courseRepository.save(course);
	        return "smv-edit-class-confirmation";
	    }
		
		
		

}
