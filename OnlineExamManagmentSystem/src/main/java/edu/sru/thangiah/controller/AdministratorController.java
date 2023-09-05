package edu.sru.thangiah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdministratorController {

    @GetMapping("/administratorlogin")
    public String showLoginPage() {
        return "administratorlogin"; // Return the login.html template
    }

}
