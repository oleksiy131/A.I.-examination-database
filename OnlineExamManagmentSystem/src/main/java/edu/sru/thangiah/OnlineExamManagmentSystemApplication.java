/**
 * ----------------------------------------------------------------------------
 * File: OnlineExamManagementSystemApplication.java
 * Date: 9/5/2023
 * Class:CPSC 488
 * Professor: Dr. Thangiah
 * Authors: Seth Chritzman, Brent Kosior, Oleksii Dukhovenko
 * ----------------------------------------------------------------------------
 * Description:
 *
 * Online Exam-taking software with different user permissions. 
 *
 * ----------------------------------------------------------------------------
 */

package edu.sru.thangiah;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.RoleRepository;
import edu.sru.thangiah.repository.UserRepository;

@SpringBootApplication
public class OnlineExamManagmentSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(OnlineExamManagmentSystemApplication.class, args);
	}
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	 List<Roles> rolesList = new ArrayList<>();

	
	
	@Bean
	public CommandLineRunner setupRoles(RoleRepository roleRepository) {
		

	    return args -> {
	        // Check and insert roles if they don't exist
	        if (!roleRepository.findByName("ADMINISTRATOR").isPresent()) {
	            roleRepository.save(new Roles(null, "ADMINISTRATOR"));
	        }
	        if (!roleRepository.findByName("STUDENT").isPresent()) {
	            roleRepository.save(new Roles(null, "STUDENT"));
	        }
	        if (!roleRepository.findByName("INSTRUCTOR").isPresent()) {
	            roleRepository.save(new Roles(null, "INSTRUCTOR"));
	        }
	        if (!roleRepository.findByName("SCHEDULE_MANAGER").isPresent()) {
	            roleRepository.save(new Roles(null, "SCHEDULE_MANAGER"));
	        }
	    };
	}
	    @Bean
	    public CommandLineRunner setupDefaultUser(UserRepository userRepository, RoleRepository roleRepository) {
	        return args -> {
	            User root = new User(null, null, null, null, null, null, null, false, null);
	            root.setUsername("root");
	            
				root.setPassword(passwordEncoder.encode("software"));
	            
	            Roles adminRole = roleRepository.findByName("ADMINISTRATOR")
	                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	            	rolesList.add(adminRole);
	            	root.setEnabled(true);
	            	root.setRoles(rolesList);
	            
	            if (!userRepository.findByUsername(root.getUsername()).isPresent()) {
	                userRepository.save(root);
	            }
	        };
	   

	}
	
	

}



