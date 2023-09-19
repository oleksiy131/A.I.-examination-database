package edu.sru.thangiah.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.UserRepository;
import edu.sru.thangiah.service.EmailService;
import edu.sru.thangiah.service.UserService;
import edu.sru.thangiah.web.dto.UserRegistrationDto;

@Controller
public class MenuController {
	
		@Autowired
	    private UserService userService;

	    @Autowired
	    private EmailService emailService;
	
	@RequestMapping("/navbar")
    public String showMainScreen() {
        return "navbar"; 
    }
	
	@RequestMapping("/sidebar")
    public String showSidebar() {
        return "sidebar"; 
    }
	
	@RequestMapping("/exit")
    public String exitProgram() {
        return "index"; 
    }

	@Autowired
    private UserRepository userRepository;

	
	// this will pull data from the user, the user enters this data on the register.html page
	
    
}

