/*
 *   /$$$$$$  /$$       /$$$$$$$$ /$$   /$$  /$$$$$$  /$$$$$$ /$$$$$$
 /$$__  $$| $$      | $$_____/| $$  /$$/ /$$__  $$|_  $$_/|_  $$_/
| $$  \ $$| $$      | $$      | $$ /$$/ | $$  \__/  | $$    | $$  
| $$  | $$| $$      | $$$$$   | $$$$$/  |  $$$$$$   | $$    | $$  
| $$  | $$| $$      | $$__/   | $$  $$   \____  $$  | $$    | $$  
| $$  | $$| $$      | $$      | $$\  $$  /$$  \ $$  | $$    | $$  
|  $$$$$$/| $$$$$$$$| $$$$$$$$| $$ \  $$|  $$$$$$/ /$$$$$$ /$$$$$$
 \______/ |________/|________/|__/  \__/ \______/ |______/|______/
 */


 package edu.sru.thangiah.controller;
 

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/schedule-manager")
public class ScheduleManagerController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;
    
    @GetMapping("/add-course")
    public String showCreateCourseForm() {
        return "add-course"; // This corresponds to the name of your HTML file
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
    public String loadManagerPage() {
        return "manager"; // This corresponds to the name of your HTML file
    }

    // Load/Create Instructor
    @PostMapping("/instructor/add")
    public ResponseEntity<?> addInstructor(@ModelAttribute Instructor instructor) {
        try {
            if (instructorRepository.findById(instructor.getInstructorId()).isPresent()) {
                return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Instructor with given ID already exists.\"}");
            }

            Instructor savedInstructor = instructorRepository.save(instructor);
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Instructor added.\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Failed to add instructor.\"}");
        }
    }


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

    @PostMapping("/instructor/edit")
    public ResponseEntity<?> editInstructor(@ModelAttribute Instructor updatedInstructor) {
        try {
            // Find the instructor in the database
            Optional<Instructor> existingInstructorOpt = instructorRepository.findById(updatedInstructor.getInstructorId());

            if (existingInstructorOpt.isPresent()) {
                Instructor existingInstructor = existingInstructorOpt.get();

                // Update the instructor details
                existingInstructor.setInstructorFirstName(updatedInstructor.getInstructorFirstName());
                existingInstructor.setInstructorLastName(updatedInstructor.getInstructorLastName());
                existingInstructor.setInstructorEmail(updatedInstructor.getInstructorEmail());
                existingInstructor.setInstructorPassword(updatedInstructor.getInstructorPassword());
                existingInstructor.setInstructorUsername(updatedInstructor.getInstructorUsername());
                existingInstructor.setCreditsTaught(updatedInstructor.getCreditsTaught());

                instructorRepository.save(existingInstructor);
                return new ResponseEntity<>(existingInstructor, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Instructor not found!", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while updating the instructor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Delete instructor
    @DeleteMapping("/instructor/delete/{instructorId}")
    public ResponseEntity<?> deleteInstructor(@PathVariable Long instructorId) {
        try {
            if (!instructorRepository.existsById(instructorId)) {
                return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Invalid instructor ID.\"}");
            }

            instructorRepository.deleteById(instructorId);
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Instructor deleted.\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Failed to delete instructor.\"}");
        }
    }

    // Edit course details - similar to the edit instructor endpoint.
    // Additional endpoints for deleting/editing courses can also be added similarly.
}
