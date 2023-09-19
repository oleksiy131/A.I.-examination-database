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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
 class OnlineExamManagmentSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineExamManagmentSystemApplication.class, args);
	}
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Bean
	public CommandLineRunner setupDefaultUser() {
	    return args -> {
	        User root = new User(null, null, null, null, null, null, null, null);
	        root.setUsername("root");
	        root.setPassword(passwordEncoder.encode("software"));
	        root.setRole(User.Role.ADMINISTRATOR);
	        
	        if (userRepository.findByUsername(root.getUsername()).isEmpty()) {
	            userRepository.save(root);
	        }
	    };
	}

}


