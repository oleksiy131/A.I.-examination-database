package edu.sru.thangiah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.GetMapping;

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
}
