package edu.sru.thangiah.controller;

import edu.sru.thangiah.domain.Administrator;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MenuController {
	


	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private RoleRepository roleRepository;

	    @Autowired
	    private AdministratorRepository administratorRepository;

	    @Autowired
	    private StudentRepository studentRepository;

	    @Autowired
	    private InstructorRepository instructorRepository;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @GetMapping("/index")
	    public String login() {
	        return "index"; 
	    }

		
		  @GetMapping("/register") public String register(Model model) {
		  model.addAttribute("user", new User()); return "register"; }
		 

	    @PostMapping("/register")
	    public String saveUser(User user, @RequestParam String role) {
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        Roles userRole = roleRepository.findByName(role)
	            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	        user.setRole(userRole);
	        userRepository.save(user);

	        switch (role) {
	            case "ADMINISTRATOR":
	                Administrator admin = new Administrator();
	                admin.setUser(user);
	                administratorRepository.save(admin);
	                break;
	            case "STUDENT":
	                Student student = new Student();
	                student.setUser(user);
	                studentRepository.save(student);
	                break;
	            case "INSTRUCTOR":
	                Instructor instructor = new Instructor();
	                instructor.setUser(user);
	                instructorRepository.save(instructor);
	                break;
	            // Add Scedule manager when merged
	        }

	        return "redirect:/index"; // Redirect to login page after successful registration
	    }
	
	
	@RequestMapping("/navbar")
    public String showMainScreen() {
        return "navbar"; 
    }
	
	
	
	@RequestMapping("/sidebar")
    public String showSidebar() {
	    System.out.println("Sidebar request received");
        return "sidebar"; 
    }
	
	@RequestMapping("/exit")
    public String exitProgram() {
        return "index"; 
    }


    
}