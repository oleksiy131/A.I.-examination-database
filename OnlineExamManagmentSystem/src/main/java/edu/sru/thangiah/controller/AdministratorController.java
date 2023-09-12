package edu.sru.thangiah.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.thangiah.domain.Administrator;
import edu.sru.thangiah.repository.AdministratorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class AdministratorController {

    @Value("${spring.datasource.username}")
    private String adminUsername;

    @Value("${spring.datasource.password}")
    private String adminPassword;

    @Autowired
    private AdministratorRepository administratorRepository;

    @GetMapping("/administratorlogin")
    public String showLoginPage() {
        return "administratorlogin"; // Return the administratorlogin.html template
    }

    @PostMapping("/adminlogin")
    public String adminLogin(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        RedirectAttributes redirectAttributes
    ) {
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            // Successful login, redirect to the navbar screen
            return "redirect:/navbar";
        } else {
            // Invalid credentials, display an error message
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid credentials");
            return "redirect:/administratorlogin";
        }
    }

    // This method handles HTTP POST requests to create a new Administrator.
    @PostMapping("/process_login")
    public Administrator createAdministrator(@RequestBody Administrator administrator) {
        // Save the Administrator object to the repository and return the saved user.
        return administratorRepository.save(administrator);
    }
    
    @GetMapping("/exams")
    public String examsPage() {
        // display the list of exams here
        return "exams"; // the name of the HTML template for the exams page
    }
    
    @GetMapping("/classes")
    public String classesPage() {
        // displays the list of classes here
        return "classes"; // the name of the HTML template for the classes page
    }
    
    @GetMapping("/math-quiz")
    public String mathQuizPage() {
        // displays the math quiz
        return "math-quiz"; // the name of the HTML template for the quiz page
    }
    
}

