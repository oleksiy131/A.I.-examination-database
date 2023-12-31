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
import edu.sru.thangiah.domain.ScheduleManager;
import edu.sru.thangiah.domain.Student;
import edu.sru.thangiah.model.Roles;
import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.AdministratorRepository;
import edu.sru.thangiah.repository.InstructorRepository;
import edu.sru.thangiah.repository.RoleRepository;
import edu.sru.thangiah.repository.ScheduleManagerRepository;
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
    
    @Autowired
    private ScheduleManagerRepository scheduleManagerRepository;

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
            createUserIfNotFound("root", "software", "ADMINISTRATOR", "admin@sru.edu", "Default", "Root");
            createUserIfNotFound("student", "software", "STUDENT", "student@sru.edu", "Default", "Student");
            createUserIfNotFound("instructor", "software", "INSTRUCTOR", "instructor@sru.edu", "Default", "Instructor");
            createUserIfNotFound("schedulemanager", "software", "SCHEDULE_MANAGER", "manager@sru.edu", "Default", "Manager");
        };
    }


    private void createRoleIfNotFound(String roleName) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            roleRepository.save(new Roles(null, roleName));
        }
    }

    private void createUserIfNotFound(String username, String password, String roleName, String email, String firstName, String lastName) {
        if (!userRepository.findByUsername(username).isPresent()) {
        	User user = new User();
            user.setUsername(username);// set the username
            user.setPassword(passwordEncoder.encode(password));//set the password
            user.setEmail(email); // Set the email
            user.setFirstName(firstName); // Set the first name
            user.setLastName(lastName); // Set the last name

            Roles role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            
            List<Roles> rolesList = new ArrayList<>();
            rolesList.add(role);
            user.setEnabled(true);
            user.setRoles(rolesList);
            
            userRepository.save(user);

            if (roleName.equals("ADMINISTRATOR")) {
                Administrator admin = new Administrator();
                admin.setUser(user);
                admin.setAdminPassword(passwordEncoder.encode(password));
                admin.setAdminEmail("admin@sru.edu");
                admin.setAdminFirstName("Default");
                admin.setAdminLastName("Root");
                administratorRepository.save(admin);
            }
            if (roleName.equals("STUDENT")) {
                Student student = new Student();
                student.setStudentId((long) 2L);
                student.setUser(user);
                student.setStudentPassword(passwordEncoder.encode(password));
                student.setStudentEmail("student@sru.edu");
                student.setStudentFirstName("Default");
                student.setStudentLastName("Student");
                studentRepository.save(student);
            }
            if (roleName.equals("INSTRUCTOR")) {
                Instructor instructor = new Instructor();
                instructor.setInstructorId((long) 3L);
                instructor.setUser(user);
                instructor.setInstructorPassword(passwordEncoder.encode(password));
                instructor.setInstructorEmail("instructor@sru.edu");
                instructor.setInstructorFirstName("Default");
                instructor.setInstructorLastName("Instructor");
                instructorRepository.save(instructor);
            }
            
            if (roleName.equals("SCHEDULE_MANAGER")) {
                ScheduleManager scheduleManager = new ScheduleManager();
                scheduleManager.setUser(user);
                scheduleManager.setManagerUsername(username);
                scheduleManager.setManagerPassword(passwordEncoder.encode(password));
                scheduleManager.setManagerEmail("sm@sru.edu");
                scheduleManager.setManagerFirstName("Default");
                scheduleManager.setManagerLastName("Manager");
                scheduleManagerRepository.save(scheduleManager);
            }
        }
    }
}