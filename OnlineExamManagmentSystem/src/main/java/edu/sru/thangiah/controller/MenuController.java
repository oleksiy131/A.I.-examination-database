package edu.sru.thangiah.controller;


//import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.UserRepository;
import edu.sru.thangiah.service.EmailVerificationService;

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
	    // Create a new User object with the required data
	    User user = new User(null, null, null, null, null, null, null);

	    // Add the user object to the model if needed for your view
	    model.addAttribute("user", user);

	    return "redirect:register";
	}
    //this moves the data to the userRepository that stores the data in our SQL server.
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userRepository.save(user);
        
        //EmailVerifyController emailVerifyController = new EmailVerifyController(null);
		// Pass the user object to the EmailVerifyController method
	    EmailVerifyController.sendMail();
        
        return "navbar";
    }
    
}

