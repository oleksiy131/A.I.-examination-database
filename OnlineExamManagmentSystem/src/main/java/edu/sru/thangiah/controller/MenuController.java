package edu.sru.thangiah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {
	@GetMapping("/navbar")
    public String showLoginPage() {
        return "navbar"; // Return the login.html template
    }
}
