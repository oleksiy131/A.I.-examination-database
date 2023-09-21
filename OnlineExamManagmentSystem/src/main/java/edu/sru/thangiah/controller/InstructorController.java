package edu.sru.thangiah.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;

@RestController
@RequestMapping("/instructor")
public class InstructorController {

    private  InstructorRepository instructorRepository;
    private  CourseRepository courseRepository;


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
    
    
    
    

   
}


