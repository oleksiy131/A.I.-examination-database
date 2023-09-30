package edu.sru.thangiah.controller;
 

import edu.sru.thangiah.domain.Course;

import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.exception.ResourceNotFoundException;
import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.RoleRepository;
import edu.sru.thangiah.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private InstructorRepository instructorRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;

    
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/add-course")
    public String showCreateCourseForm() {
        return "add-course"; 
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
    
    
    @GetMapping("/instructors")
    @ResponseBody
    public List<Instructor> getInstructors() {
        return instructorRepository.findAll();
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
    @PostMapping("/instructor/add")
    public String addInstructor(@ModelAttribute Instructor instructor, RedirectAttributes redirectAttributes) {
        System.out.println("Inside instructor-add method");
        try {
            // Check if the instructor with the given username already exists
            if (instructorRepository.findByInstructorUsername(instructor.getInstructorUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Instructor with given username already exists.");
                return "redirect:/schedule-manager/create-instructor";
            }

            // Fetch the role with ID 3 and set it to the instructor
            Roles role = roleRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Role with ID 3 not found"));
            instructor.setRole(role);

            // Save the new instructor
            instructorRepository.save(instructor);

            // Create and save the corresponding user
            User newUser = new User();
            newUser.setId(instructor.getInstructorId());
            newUser.setUsername(instructor.getInstructorUsername());
            newUser.setPassword(instructor.getInstructorPassword());  // We might want to encode this
            newUser.setRole(role); 

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
                existingInstructor.setInstructorPassword(updatedInstructor.getInstructorPassword());  // Consider encrypting the password
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

}
