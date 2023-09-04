package edu.sru.thangiah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.sru.thangiah.oems.domain.Student;
import edu.sru.thangiah.oems.repository.StudentRepository;

/*
 * CRUD functionality to the web application can be added through a basic web tier. The UserController
 * class is used to handling GET and POST HTTP requests. The requests are mapped to methods in the UserController.
 */

@Controller
public class StudentController {
	//set up a UserRepositoty variable

	private StudentRepository studentRepository;
    
	//create an UserRepository instance - instantiation (new) is done by Spring
    public StudentController(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}
    
    //Mapping for the /index URL when initiated through Tomcat
    @RequestMapping({"/index"})
    public String showUserList(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "users";
    }

    //Mapping for the /signup URL - calls the add-user HTML, to add a user
	@RequestMapping({"/signup"})
    public String showSignUpForm(Student user) {
        return "signup_form";
    }
    
	//Mapping for the /signup URL - to add a user
    @RequestMapping({"/adduser"})
    public String addUser(@Validated Student user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "registration_success";
        }
        
        studentRepository.save(user);
        return "redirect:/index";
    }
    
  
    //Mapping for the /edit/user URL to edit a user 
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
    	Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        
        model.addAttribute("student", student);
        return "update-student";
    }
    
    //Mapping for the /update/id URL to update a user 
    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable("id") long id, @Validated Student student, 
      BindingResult result, Model model) {
        if (result.hasErrors()) {
            student.setId(id);
            return "update-student";
        }
            
        studentRepository.save(student);
        return "redirect:/index";
    }
    
    //Mapping for the /delete/id URL to delete a user     
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") long id, Model model) {
    	Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
    	studentRepository.delete(student);
        return "redirect:/index";
    }
}
