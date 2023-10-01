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
 * May the Lord bless this program, and make it run with no errors,
 * forever and ever,
 * Amen
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

import edu.sru.thangiah.domain.Administrator;
import edu.sru.thangiah.domain.Instructor;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.AdministratorRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.RoleRepository;
import edu.sru.thangiah.repository.StudentRepository;
import edu.sru.thangiah.repository.UserRepository;

@SpringBootApplication
public class OnlineExamManagmentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineExamManagmentSystemApplication.class, args);
    }
    
    //----------------------Setting up the database--------------------\\

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdministratorRepository administratorRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private InstructorRepository instructorRepository;

    @Bean
    public CommandLineRunner setupRoles() {
        return args -> {
            // Check and insert roles if they don't exist
            createRoleIfNotFound("ADMINISTRATOR");
            createRoleIfNotFound("STUDENT");
            createRoleIfNotFound("INSTRUCTOR");
            createRoleIfNotFound("SCHEDULE_MANAGER");
        };
    }

    @Bean
    public CommandLineRunner setupDefaultUser() {
        return args -> {
            createUserIfNotFound("root", "software", "ADMINISTRATOR");
        };
    }

    private void createRoleIfNotFound(String roleName) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            roleRepository.save(new Roles(null, roleName));
        }
    }

    private void createUserIfNotFound(String username, String password, String roleName) {
        if (!userRepository.findByUsername(username).isPresent()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));

            Roles role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            
            user.setRole(role);
            List<Roles> rolesList = new ArrayList<>();
            rolesList.add(role);
            user.setEnabled(true);//sets all users to enabled account
            user.setRoles(rolesList);
            
            userRepository.save(user);

            if (roleName.equals("ADMINISTRATOR")) {
                Administrator admin = new Administrator();
                admin.setUser(user);
                administratorRepository.save(admin);
            }
            if (roleName.equals("STUDENT")) {
                Student student = new Student();
                student.setUser(user);
                studentRepository.save(student);
            }
            if (roleName.equals("INSTRUCTOR")) {
                Instructor instructor = new Instructor();
                instructor.setUser(user);
                instructorRepository.save(instructor);
            }
			/*
			 * if (roleName.equals("SCHEDULE_MANAGER")) { ScheduleManager scheduleManager =
			 * new ScheduleManager(); scheduleManager.setUser(user);
			 * scheduleManagerRepository.save(scheduleManager); }
			 */
            
        }
    }
}