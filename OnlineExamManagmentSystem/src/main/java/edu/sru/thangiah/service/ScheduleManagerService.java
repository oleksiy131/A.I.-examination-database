package edu.sru.thangiah.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.InstructorRepository;

@Service
public class ScheduleManagerService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    public Instructor createOrUpdateInstructor(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public Course createOrUpdateCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteInstructor(Long id) {
        instructorRepository.deleteById(id);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
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

    
}
