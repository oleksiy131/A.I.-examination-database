package edu.sru.thangiah.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instructors")
public class InstructorController {
	
	@GetMapping("/math-quiz")
    public String mathQuizPage() {
        // displays the math quiz
        return "math-quiz"; // the name of the HTML template for the quiz page
    }

}

