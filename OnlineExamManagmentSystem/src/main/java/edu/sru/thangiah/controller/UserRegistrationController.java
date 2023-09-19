package edu.sru.thangiah.controller;

import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.sru.thangiah.service.UserService;
import edu.sru.thangiah.web.dto.UserRegistrationDto;

@Controller
//@RequestMapping("/register")
public class UserRegistrationController {
	
	private UserService userService;

	public UserRegistrationController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	/*
	//@GetMapping
	public String showRegistrationForm() {
		return "register";
	}
	
	@PostMapping
	public String registerUserAccount(@ModelAttribute("user")UserRegistrationDto registrationDto) {
		userService.save(registrationDto);
		return "redirect:/index";//registration?success";
		
	}
	*/
}
