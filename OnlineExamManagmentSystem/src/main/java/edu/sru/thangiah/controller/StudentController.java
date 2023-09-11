package edu.sru.thangiah.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sru.thangiah.repository.CourseRepository;
import edu.sru.thangiah.repository.StudentRepository;

@RestController
@RequestMapping("/student/course")
public class StudentController {
	
	private StudentRepository studentRepository;
	private CourseRepository courseRepository;

    @GetMapping("/studentlogin")
    public String showLoginPage() {
        return "studentlogin"; // Return the login.html template
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register"; // Return the register.html template
    }
    
    
}
