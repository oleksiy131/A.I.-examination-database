package edu.sru.thangiah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InstructorController {

    @GetMapping("/instructorlogin")
    public String showLoginPage() {
        return "instructorlogin"; // Return the login.html template
    }

}
