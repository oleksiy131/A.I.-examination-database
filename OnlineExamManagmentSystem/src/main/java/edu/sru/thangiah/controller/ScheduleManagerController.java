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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;




/*
 *____  __    __        _ _ 
 / __ \/ /__ / /__ ___ (_|_)
/ /_/ / / -_)  '_/(_-</ / / 
\____/_/\__/_/\_\/___/_/_/  
                        
 */

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
    
	@RequestMapping("/schedule_manager_homepage")
	public String showScheduleManagerHomepage() {
		return "schedule_manager_homepage";
	}

    
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/add-course")
    public String showCreateCourseForm() {
        return "add-course"; 
    }
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/add-courseSM")
    public String showCreateCourseFormSMV() {
        return "smv-add-course"; 
    }
    
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/instructor-list")
    public String showInstructorList(Model model) {
        List<Instructor> instructors = instructorRepository.findAll();
        model.addAttribute("instructors", instructors);
        return "instructor-list";
    }
    
    @PreAuthorize("hasRole('ADMINISTRATOR')")
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
	public ResponseEntity<String> handleAssociateStudentWithCourse(@RequestParam("studentId") Long studentId, @RequestParam("courseId") Long courseId) {
	    return new ResponseEntity<>("Student associated with course successfully!", HttpStatus.OK);
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
	public ResponseEntity<String> handleAssociateInstructorWithCourse(@RequestParam("instructorId") Long studentId, @RequestParam("courseId") Long courseId) {
	    return new ResponseEntity<>("Instructor associated with course successfully!", HttpStatus.OK);
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

    
    @GetMapping("/associate-instructor")
    public String associateInstructorWithCourseForm(Model model) {
        // Retrieve the list of instructors and courses from the repository
        List<Instructor> instructors = instructorRepository.findAll();
        List<Course> courses = courseRepository.findAll();

        // Add the lists of instructors and courses to the model for rendering in the HTML template
        model.addAttribute("instructors", instructors);
        model.addAttribute("courses", courses);

        return "associate-instructor";
    }
    
    @PostMapping("/associate-instructor")
    public String associateInstructorWithCourse(
            @RequestParam Long instructorId,
            @RequestParam Long courseId,
            RedirectAttributes redirectAttributes) {

        Optional<Instructor> optionalInstructor = instructorRepository.findById(instructorId);
        Optional<Course> optionalCourse = courseRepository.findById(courseId);

        if (optionalInstructor.isPresent() && optionalCourse.isPresent()) {
            Instructor instructor = optionalInstructor.get();
            Course course = optionalCourse.get();

            // Assuming the association is bidirectional
            instructor.getCourses().add(course);
            course.setInstructor(instructor);

            instructorRepository.save(instructor);
            courseRepository.save(course);
            redirectAttributes.addFlashAttribute("successMessage", "Instructor successfully associated with the course");
        } else {
            redirectAttributes.addFlashAttribute("failureMessage", "Failed to associate instructor with the course");
        }

        return "redirect:/success";
    }



    
    @GetMapping("/edit-instructor")
    public String editInstructorForm(Model model, @RequestParam(required = false) Long instructorId) {
        List<Instructor> instructors = instructorRepository.findAll();
        model.addAttribute("instructors", instructors);

        if (instructorId != null) {
            Optional<Instructor> selectedInstructor = instructorRepository.findById(instructorId);
            if (selectedInstructor.isPresent()) {
                model.addAttribute("selectedInstructor", selectedInstructor.get());
            }
        }

        return "edit-instructor"; 
    }

    
    
    @GetMapping("/page")
    public String loadManagerPage(Model model) {
        List<Instructor> instructors = instructorRepository.findAll();
        model.addAttribute("instructors", instructors);
        return "manager"; 
    }

    // Load/Create Instructor
    @Transactional
    @PostMapping("/schedule-manager/add")
    public String addInstructor(@ModelAttribute Instructor instructor, RedirectAttributes redirectAttributes) {
        System.out.println("Inside instructor-add method");
        try {
            // Check if the instructor with the given username already exists
            if (instructorRepository.findByInstructorUsername(instructor.getInstructorUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Instructor with given username already exists.");
                return "redirect:/schedule-manager/create-instructor";
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
            return "redirect:/instructor-success";
        } catch (Exception e) {
            System.out.println("Failed to add instructor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add instructor.");
            return "redirect:/fail";
        }
    }
    

	@GetMapping("/smv-instructor-success")
	public String showInstructorSuccessFormSMV() {
		return "smv-instructor-success";
	}
    
    // Load/Create Instructor
    @Transactional
    @PostMapping("/add")
    public String addInstructorSMV(@ModelAttribute Instructor instructor, RedirectAttributes redirectAttributes) {
        System.out.println("Inside instructor-add method");
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
    @PreAuthorize("hasRole('ADMINISTRATOR')")
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
    @PreAuthorize("hasRole('ADMINISTRATOR')")
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

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/instructor/edit")
    public String editInstructor(@ModelAttribute Instructor updatedInstructor, RedirectAttributes redirectAttributes) {
    	System.out.println("Inside Instructor Edit");
        try {
            // Find the instructor in the database
            Optional<Instructor> existingInstructorOpt = instructorRepository.findById(updatedInstructor.getInstructorId());

            if (existingInstructorOpt.isPresent()) {
                Instructor existingInstructor = existingInstructorOpt.get();

                // Update the instructor details
                existingInstructor.setInstructorFirstName(updatedInstructor.getInstructorFirstName());
                existingInstructor.setInstructorLastName(updatedInstructor.getInstructorLastName());
                existingInstructor.setInstructorEmail(updatedInstructor.getInstructorEmail());
                existingInstructor.setInstructorPassword(passwordEncoder.encode(updatedInstructor.getInstructorPassword()));
                existingInstructor.setInstructorUsername(updatedInstructor.getInstructorUsername());
                existingInstructor.setCreditsTaught(updatedInstructor.getCreditsTaught());

                instructorRepository.save(existingInstructor);
                redirectAttributes.addFlashAttribute("successMessage", "Instructor updated successfully.");
                return "redirect:/schedule-manager/edit-instructor";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Instructor not found.");
                return "redirect:/schedule-manager/edit-instructor";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("Message", "Instructor updated successfully.");
            return "redirect:/schedule-manager/page";
        }
    }

    // Delete instructor
    @PostMapping("/instructor/delete")	//This is VERY rough but i am working on this...
    public ResponseEntity<?> deleteInstructor(@RequestParam Long instructorId) {
    	
    	boolean association = false;
    	
        try {
            Instructor instructor = instructorRepository.findById(instructorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found: " + instructorId));

            // Fetch the courses associated with this instructor
            List<Course> courses = courseRepository.findAllByInstructor(instructor);

            // to prevent deletion if the instructor is associated with any courses
            if (!courses.isEmpty()) {
            	association = true;
            	
            	 //  disassociate the instructor from their courses before deletion
                for (Course course : courses) {
                    course.setInstructor(null);
                    courseRepository.save(course);
                }
                
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("{\"success\": false, \"message\": \"WARNING: Instructor is associated with one or more courses. The association is now removed. Refresh the page to DELETE the instructor.\"}");
                
            }


            instructorRepository.deleteById(instructorId);
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Instructor deleted successfully.\"}");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\": false, \"message\": \"Failed to delete instructor.\"}");
        }
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
    public String showUpdateFormAV(@PathVariable("id") long id, Model model) {
		Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        
        model.addAttribute("student", student);
        return "smv-edit-student";
    }

	@PostMapping("/smv-update/{id}")
    public String updateStudentAV(@PathVariable("id") long id, @Validated Student student, 
      BindingResult result, Model model) {
        if (result.hasErrors()) {
            student.setStudentId(id);
            return "smv-update-user";
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
        return "smv-edit-confirmation";
    }
	
	@GetMapping("/student/delete/{id}")
    public String deleteStudentSMV(@PathVariable("id") long id, Model model) {
        Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        studentRepository.delete(student);
        return "smv-edit-confirmation";
    }
}

