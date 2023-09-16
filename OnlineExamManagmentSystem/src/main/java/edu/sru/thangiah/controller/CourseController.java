package edu.sru.thangiah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import edu.sru.thangiah.domain.Course;
import edu.sru.thangiah.repository.CourseRepository;

@Controller
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping("/add-course")
    @ResponseBody
    public ResponseEntity<?> addCourse(@RequestBody Course course) {
        try {
            // Save the course to the database
            Course savedCourse = courseRepository.save(course);
            if (savedCourse != null) {
                return ResponseEntity.ok().body("{\"success\": true}");
            } else {
                return ResponseEntity.ok().body("{\"success\": false}");
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body("{\"success\": false}");
        }
    }

}
