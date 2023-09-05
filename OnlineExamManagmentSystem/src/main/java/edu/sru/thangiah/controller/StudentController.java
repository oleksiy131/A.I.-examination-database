package edu.sru.thangiah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

    @GetMapping("/studentlogin")
    public String showLoginPage() {
        return "studentlogin"; // Return the login.html template
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register"; // Return the register.html template
    }
}
