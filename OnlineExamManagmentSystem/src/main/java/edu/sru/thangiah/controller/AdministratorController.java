package edu.sru.thangiah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import edu.sru.thangiah.oems.domain.Administrator;
import edu.sru.thangiah.oems.repository.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdministratorController {

    @GetMapping("/administratorlogin")
    public String showLoginPage() {
        return "administratorlogin"; // Return the login.html template
    }
    
    @Autowired
    private AdministratorRepository administratorRepository;

    // This method handles HTTP POST requests to create a new Administrator.
    @PostMapping("/create")
    public Administrator createAdministrator(@RequestBody Administrator administrator) {
        // Save the Administrator object to the repository and return the saved user.
        return administratorRepository.save(administrator);
    }
    

}
