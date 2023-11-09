package edu.sru.thangiah.controller;

import org.springframework.stereotype.Controller;

import edu.sru.thangiah.service.UserService;

@Controller
//@RequestMapping("/register")
public class UserRegistrationController {
	
	private UserService userService;

	public UserRegistrationController(UserService userService) {
		super();
		this.userService = userService;
	}

}
