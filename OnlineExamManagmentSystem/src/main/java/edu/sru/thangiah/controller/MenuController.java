package edu.sru.thangiah.controller;


import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.UserRepository;
//import org.springframework.web.bind.annotation.GetMapping;
import edu.sru.thangiah.service.UserVerifyService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MenuController {
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
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User(null, null, null, null, null, null, null));
        return "register";
    }
    //this moves the data to the userRepository that stores the data in our SQL server.
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:index";
    }
    
/*
    @Autowired
    private UserVerifyService service;
 
    /*
     * this is still being worked on, its part of email verification.
     * 
     * 
     * */
    /*
    @PostMapping("/register")
    public String processRegister(User user, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException, javax.mail.MessagingException {
        service.register(user, getSiteURL(request));
        return "registration_success";
    }
 
    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }  
 */    
}

